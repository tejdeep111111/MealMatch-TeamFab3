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

data class MealDetailsState(
    val loading: Boolean = false,
    val meal: Meal? = null,
    val userTags: List<String> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class MealDetailsViewModel @Inject constructor(
    private val repo: MealRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _state = MutableStateFlow(MealDetailsState())
    val state = _state.asStateFlow()

    fun load(mealId: String) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val token = tokenStore.tokenFlow.first().orEmpty()
                // Backend has no single-meal endpoint; fetch all and find by id
                val meal = repo.meals(token).firstOrNull { it.id == mealId }
                val tagsString = try { repo.getDietaryTags(token) } catch (_: Exception) { "" }
                val userTags = tagsString.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                if (meal != null) {
                    _state.update { it.copy(loading = false, meal = meal, userTags = userTags) }
                } else {
                    _state.update { it.copy(loading = false, error = "Meal not found", userTags = userTags) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message ?: "Failed to load meal") }
            }
        }
    }
}
