package com.teamfab.meallmatch.person.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamfab.meallmatch.person.data.MealRepository
import com.teamfab.meallmatch.person.data.local.TokenStore
import com.teamfab.meallmatch.person.data.model.SubscriptionResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SubscriptionsState(
    val loading: Boolean = true,
    val subscriptions: List<SubscriptionResponse> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val repo: MealRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _state = MutableStateFlow(SubscriptionsState())
    val state = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val token = tokenStore.tokenFlow.first().orEmpty()
                val subs = repo.mySubscriptions(token)
                _state.update { it.copy(loading = false, subscriptions = subs) }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message ?: "Failed to load subscriptions") }
            }
        }
    }

    fun pause(id: String) = performAction { repo.pauseSubscription(it, id) }
    fun resume(id: String) = performAction { repo.resumeSubscription(it, id) }
    fun cancel(id: String) = performAction { repo.cancelSubscription(it, id) }

    private fun performAction(action: suspend (String) -> SubscriptionResponse) {
        viewModelScope.launch {
            try {
                val token = tokenStore.tokenFlow.first().orEmpty()
                action(token)
                load() // refresh
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Action failed") }
            }
        }
    }
}

