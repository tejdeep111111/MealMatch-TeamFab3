package com.teamfab.meallmatch.person.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamfab.meallmatch.person.data.MealRepository
import com.teamfab.meallmatch.person.data.local.TokenStore
import com.teamfab.meallmatch.person.data.model.Meal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val loading: Boolean = true,
    val allMeals: List<Meal> = emptyList(),
    val meals: List<Meal> = emptyList(),           // displayed list (filtered or all)
    val userTags: List<String> = emptyList(),
    val showOnlyCompatible: Boolean = true,
    val selectedCategory: String = "All",
    val searchQuery: String = "",
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

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val token = tokenStore.tokenFlow.first().orEmpty()

                val allMeals = repo.meals(token)
                val tagsString = try { repo.getDietaryTags(token) } catch (_: Exception) { "" }
                val userTags = tagsString.split(",").map { it.trim() }.filter { it.isNotEmpty() }

                _state.update { s ->
                    val showCompat = userTags.isNotEmpty()
                    s.copy(
                        loading = false,
                        allMeals = allMeals,
                        userTags = userTags,
                        showOnlyCompatible = showCompat,
                        meals = applyFilters(allMeals, userTags, showCompat, s.selectedCategory, s.searchQuery)
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
                meals = applyFilters(s.allMeals, s.userTags, show, s.selectedCategory, s.searchQuery)
            )
        }
    }

    fun selectCategory(category: String) {
        _state.update { s ->
            s.copy(
                selectedCategory = category,
                meals = applyFilters(s.allMeals, s.userTags, s.showOnlyCompatible, category, s.searchQuery)
            )
        }
    }

    fun updateSearch(query: String) {
        _state.update { s ->
            s.copy(
                searchQuery = query,
                meals = applyFilters(s.allMeals, s.userTags, s.showOnlyCompatible, s.selectedCategory, query)
            )
        }
    }

    private fun applyFilters(
        allMeals: List<Meal>,
        userTags: List<String>,
        showOnlyCompatible: Boolean,
        selectedCategory: String,
        searchQuery: String
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

        // 3. Search filter
        if (searchQuery.isNotBlank()) {
            result = result.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }

        return result
    }
}
