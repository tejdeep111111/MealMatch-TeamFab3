package com.teamfab.meallmatch.person.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamfab.meallmatch.person.data.MealRepository
import com.teamfab.meallmatch.person.data.local.TokenStore
import com.teamfab.meallmatch.person.data.model.Meal
import com.teamfab.meallmatch.person.data.model.ProviderResponse
import com.teamfab.meallmatch.person.data.model.ReviewResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProviderDetailsState(
    val loading: Boolean = true,
    val provider: ProviderResponse? = null,
    val meals: List<Meal> = emptyList(),
    val reviews: List<ReviewResponse> = emptyList(),
    val userTags: List<String> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ProviderDetailsViewModel @Inject constructor(
    private val repo: MealRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _state = MutableStateFlow(ProviderDetailsState())
    val state = _state.asStateFlow()

    fun load(providerId: String) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val token = tokenStore.tokenFlow.first().orEmpty()

                // Try fetching the individual provider; fall back to finding it in the list
                val provider = try {
                    repo.provider(token, providerId)
                } catch (_: Exception) {
                    // Individual endpoint may not exist (404) — look up from full list
                    try {
                        repo.providers(token).find { it.id == providerId }
                    } catch (_: Exception) {
                        null
                    }
                }

                // Fetch all meals and filter to this provider
                val allMeals = repo.meals(token)
                val providerMeals = allMeals.filter { it.providerId == providerId }

                // Fetch provider reviews
                val reviews = try {
                    repo.providerReviews(token, providerId)
                } catch (_: Exception) {
                    emptyList()
                }

                val tagsString = try { repo.getDietaryTags(token) } catch (_: Exception) { "" }
                val userTags = tagsString.split(",").map { it.trim() }.filter { it.isNotEmpty() }

                // Build a fallback provider from meal data if we still don't have one
                val finalProvider = provider ?: if (providerMeals.isNotEmpty()) {
                    ProviderResponse(
                        id = providerId,
                        name = providerMeals.first().providerName ?: "Provider",
                    )
                } else null

                _state.update {
                    it.copy(
                        loading = false,
                        provider = finalProvider,
                        meals = providerMeals,
                        reviews = reviews,
                        userTags = userTags
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message ?: "Failed to load provider") }
            }
        }
    }
}
