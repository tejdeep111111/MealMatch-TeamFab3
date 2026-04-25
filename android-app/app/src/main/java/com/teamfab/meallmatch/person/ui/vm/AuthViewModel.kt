package com.teamfab.meallmatch.person.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamfab.meallmatch.person.data.MealRepository
import com.teamfab.meallmatch.person.data.local.TokenStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val token: String? = null,
    val email: String? = null,
    val role: String? = null,
    val loading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: MealRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            tokenStore.tokenFlow
                .distinctUntilChanged()
                .collect { token ->
                    _state.update { it.copy(token = token) }
                }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val response = repo.login(email, password)
                _state.update { it.copy(loading = false, email = response.email, role = response.role) }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message ?: "Login failed") }
            }
        }
    }

    fun register(name: String, email: String, password: String, phone: String? = null, location: String? = null, dietaryTags: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val response = repo.register(name, email, password, phone, location, dietaryTags)
                _state.update { it.copy(loading = false, email = response.email, role = response.role) }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message ?: "Registration failed") }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repo.logout()
            _state.update { AuthState() }
        }
    }
}
