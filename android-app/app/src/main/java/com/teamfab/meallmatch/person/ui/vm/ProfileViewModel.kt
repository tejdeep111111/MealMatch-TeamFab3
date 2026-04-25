package com.teamfab.meallmatch.person.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamfab.meallmatch.person.data.MealRepository
import com.teamfab.meallmatch.person.data.local.TokenStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val loading: Boolean = true,
    val email: String = "",
    val dietaryTags: String = "",
    val activeSubscriptions: Int = 0,
    val totalReviews: Int = 0,
    val compatibleMeals: Int = 0,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: MealRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val token = tokenStore.tokenFlow.first().orEmpty()

                val tags = try { repo.getDietaryTags(token) } catch (_: Exception) { "" }
                val subs = try { repo.mySubscriptions(token) } catch (_: Exception) { emptyList() }
                val reviews = try { repo.myReviews(token) } catch (_: Exception) { emptyList() }
                val compatible = try { repo.compatibleMeals(token).size } catch (_: Exception) { 0 }

                // Extract email from token (JWT payload)
                val email = try {
                    val parts = token.split(".")
                    if (parts.size >= 2) {
                        val payload = String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE))
                        val subMatch = Regex("\"sub\"\\s*:\\s*\"([^\"]+)\"").find(payload)
                        subMatch?.groupValues?.get(1) ?: ""
                    } else ""
                } catch (_: Exception) { "" }

                val activeSubs = subs.count { it.status?.uppercase() == "ACTIVE" }

                _state.update {
                    it.copy(
                        loading = false,
                        email = email,
                        dietaryTags = tags,
                        activeSubscriptions = activeSubs,
                        totalReviews = reviews.size,
                        compatibleMeals = compatible
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message ?: "Failed to load profile") }
            }
        }
    }
}

