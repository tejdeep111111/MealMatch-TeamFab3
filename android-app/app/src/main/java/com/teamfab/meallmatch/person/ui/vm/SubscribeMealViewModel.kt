package com.teamfab.meallmatch.person.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamfab.meallmatch.person.data.MealRepository
import com.teamfab.meallmatch.person.data.local.TokenStore
import com.teamfab.meallmatch.person.data.model.Meal
import com.teamfab.meallmatch.person.data.model.SubscriptionRequest
import com.teamfab.meallmatch.person.data.model.SubscriptionResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SubscribeMealState(
    val loading: Boolean = true,
    val meal: Meal? = null,
    val submitting: Boolean = false,
    val success: SubscriptionResponse? = null,
    val error: String? = null,
    // form fields
    val selectedDays: Set<String> = setOf("MON", "TUE", "WED", "THU", "FRI"),
    val deliveryTime: String = "12:30",
    val deliveryAddress: String = "",
    val startDate: String = "",
    val endDate: String = ""
)

@HiltViewModel
class SubscribeMealViewModel @Inject constructor(
    private val repo: MealRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _state = MutableStateFlow(SubscribeMealState())
    val state = _state.asStateFlow()

    fun loadMeal(mealId: String) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val token = tokenStore.tokenFlow.first().orEmpty()
                val meal = repo.meals(token).firstOrNull { it.id == mealId }
                _state.update { it.copy(loading = false, meal = meal) }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message ?: "Failed to load meal") }
            }
        }
    }

    fun toggleDay(day: String) {
        _state.update { s ->
            val newDays = if (day in s.selectedDays) s.selectedDays - day else s.selectedDays + day
            s.copy(selectedDays = newDays)
        }
    }

    fun setDeliveryTime(time: String) {
        _state.update { it.copy(deliveryTime = time) }
    }

    fun setDeliveryAddress(address: String) {
        _state.update { it.copy(deliveryAddress = address) }
    }

    fun setStartDate(date: String) {
        _state.update { it.copy(startDate = date) }
    }

    fun setEndDate(date: String) {
        _state.update { it.copy(endDate = date) }
    }

    fun subscribe() {
        val s = _state.value
        val meal = s.meal ?: return
        if (s.selectedDays.isEmpty() || s.startDate.isBlank() || s.deliveryAddress.isBlank()) {
            _state.update { it.copy(error = "Please fill days, start date and delivery address") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(submitting = true, error = null) }
            try {
                val token = tokenStore.tokenFlow.first().orEmpty()
                val request = SubscriptionRequest(
                    providerId = meal.providerId ?: "",
                    menuItemId = meal.id,
                    daysOfWeek = s.selectedDays.joinToString(","),
                    deliveryTime = s.deliveryTime.ifBlank { null },
                    deliveryAddress = s.deliveryAddress,
                    startDate = s.startDate,
                    endDate = s.endDate.ifBlank { null }
                )
                val response = repo.subscribe(token, request)
                _state.update { it.copy(submitting = false, success = response) }
            } catch (e: Exception) {
                _state.update { it.copy(submitting = false, error = e.message ?: "Subscription failed") }
            }
        }
    }
}

