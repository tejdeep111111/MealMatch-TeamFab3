package com.teamfab.meallmatch.person.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamfab.meallmatch.person.data.MealRepository
import com.teamfab.meallmatch.person.data.local.TokenStore
import com.teamfab.meallmatch.person.data.model.Meal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class PriceSort { NONE, LOW_TO_HIGH, HIGH_TO_LOW }

data class HomeState(
    val loading: Boolean = true,
    val allMeals: List<Meal> = emptyList(),
    val meals: List<Meal> = emptyList(),           // displayed list (filtered or all)
    val userTags: List<String> = emptyList(),
    val showOnlyCompatible: Boolean = true,
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val priceSort: PriceSort = PriceSort.NONE,
    val availableCategories: List<String> = listOf("All"),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: MealRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh(force: Boolean = false) {
        // Skip re-fetch if data is already loaded and a refresh wasn't explicitly requested
        if (!force && _state.value.allMeals.isNotEmpty()) return
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val token = tokenStore.tokenFlow.first().orEmpty()

                // Fetch meals and dietary tags in parallel
                val (allMeals, tagsString) = coroutineScope {
                    val mealsDeferred = async { repo.meals(token) }
                    val tagsDeferred = async {
                        try { repo.getDietaryTags(token) } catch (_: Exception) { "" }
                    }
                    mealsDeferred.await() to tagsDeferred.await()
                }

                val userTags = tagsString.split(",").map { it.trim() }.filter { it.isNotEmpty() }

                _state.update { s ->
                    val showCompat = userTags.isNotEmpty()
                    val categories = mutableListOf("All")
                    allMeals.mapNotNull { it.mealType?.trim() }
                        .filter { it.isNotBlank() }
                        .distinct()
                        .sorted()
                        .forEach { categories.add(it) }
                    s.copy(
                        loading = false,
                        allMeals = allMeals,
                        userTags = userTags,
                        showOnlyCompatible = showCompat,
                        availableCategories = categories,
                        meals = applyFilters(allMeals, userTags, showCompat, s.selectedCategory, s.searchQuery, s.priceSort)
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message ?: "Failed to load meals") }
            }
        }
    }

    fun toggleFilter() {
        _state.update { s ->
            val show = !s.showOnlyCompatible
            s.copy(
                showOnlyCompatible = show,
                meals = applyFilters(s.allMeals, s.userTags, show, s.selectedCategory, s.searchQuery, s.priceSort)
            )
        }
    }

    fun selectCategory(category: String) {
        _state.update { s ->
            s.copy(
                selectedCategory = category,
                meals = applyFilters(s.allMeals, s.userTags, s.showOnlyCompatible, category, s.searchQuery, s.priceSort)
            )
        }
    }

    fun updateSearch(query: String) {
        _state.update { s ->
            s.copy(
                searchQuery = query,
                meals = applyFilters(s.allMeals, s.userTags, s.showOnlyCompatible, s.selectedCategory, query, s.priceSort)
            )
        }
    }

    fun cyclePriceSort() {
        _state.update { s ->
            val next = when (s.priceSort) {
                PriceSort.NONE -> PriceSort.LOW_TO_HIGH
                PriceSort.LOW_TO_HIGH -> PriceSort.HIGH_TO_LOW
                PriceSort.HIGH_TO_LOW -> PriceSort.NONE
            }
            s.copy(
                priceSort = next,
                meals = applyFilters(s.allMeals, s.userTags, s.showOnlyCompatible, s.selectedCategory, s.searchQuery, next)
            )
        }
    }

    private fun applyFilters(
        allMeals: List<Meal>,
        userTags: List<String>,
        showOnlyCompatible: Boolean,
        selectedCategory: String,
        searchQuery: String,
        priceSort: PriceSort
    ): List<Meal> {
        var result = allMeals

        // 1. Diet compatibility filter
        if (showOnlyCompatible && userTags.isNotEmpty()) {
            result = result.filter { meal ->
                val itemTags = meal.dietaryTags?.split(",")?.map { it.trim() } ?: return@filter false
                userTags.any { it in itemTags }
            }
        }

        // 2. Category filter
        if (selectedCategory != "All") {
            val normalized = selectedCategory.uppercase().trimEnd('S') // "Snacks" → "SNACK"
            result = result.filter { meal ->
                val type = meal.mealType?.uppercase()?.trimEnd('S') ?: ""
                type == normalized
            }
        }

        // 3. Search filter — searches name, provider name, and dietary tags
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim()
            result = result.filter { meal ->
                meal.name.contains(q, ignoreCase = true) ||
                (meal.providerName?.contains(q, ignoreCase = true) == true) ||
                (meal.dietaryTags?.contains(q, ignoreCase = true) == true)
            }
        }

        // 4. Price sort
        result = when (priceSort) {
            PriceSort.NONE -> result
            PriceSort.LOW_TO_HIGH -> result.sortedBy { it.price }
            PriceSort.HIGH_TO_LOW -> result.sortedByDescending { it.price }
        }

        return result
    }
}
