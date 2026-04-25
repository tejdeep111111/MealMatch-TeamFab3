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

/**
 * Dietary preference categories derived from the seed data.
 * Each category maps to one or more backend dietary_tags that get stored on the user.
 */
object DietaryOptions {

    /** High-level diet type */
    val DIET_TYPES = listOf("VEG", "NON_VEG", "MIXED")

    /** Health-goal / condition presets – each maps to the tags sent to the backend */
    val HEALTH_GOALS = mapOf(
        "Diabetic Control" to listOf("DIABETIC_CONTROL", "LOW_GI"),
        "Hypertension / Low Sodium" to listOf("HYPERTENSION", "LOW_SODIUM"),
        "Fat Loss" to listOf("FAT_LOSS", "HIGH_PROTEIN", "LOW_CAL"),
        "Muscle Gain / Gym" to listOf("MUSCLE_GAIN", "HIGH_PROTEIN", "CALORIE_DENSE"),
        "High Fiber" to listOf("HIGH_FIBER"),
        "Balanced / No Restriction" to emptyList()
    )
}

data class DietaryPreferencesState(
    val loading: Boolean = true,
    val saving: Boolean = false,
    val selectedDietType: String? = null,
    val selectedGoals: Set<String> = emptySet(),      // keys from HEALTH_GOALS
    val currentBackendTags: String = "",
    val saved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DietaryPreferencesViewModel @Inject constructor(
    private val repo: MealRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _state = MutableStateFlow(DietaryPreferencesState())
    val state = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val token = tokenStore.tokenFlow.first().orEmpty()
                val tags = repo.getDietaryTags(token)
                val tagList = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }

                // Reverse-map: figure out which diet type & goals are already selected
                val dietType = when {
                    tagList.contains("VEG") && !tagList.contains("NON_VEG") -> "VEG"
                    tagList.contains("NON_VEG") && !tagList.contains("VEG") -> "NON_VEG"
                    tagList.contains("VEG") && tagList.contains("NON_VEG") -> "MIXED"
                    else -> null
                }

                val selectedGoals = DietaryOptions.HEALTH_GOALS.filter { (_, goalTags) ->
                    goalTags.isNotEmpty() && goalTags.any { it in tagList }
                }.keys

                _state.update {
                    it.copy(
                        loading = false,
                        currentBackendTags = tags,
                        selectedDietType = dietType,
                        selectedGoals = selectedGoals
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message ?: "Failed to load preferences") }
            }
        }
    }

    fun setDietType(type: String) {
        _state.update { it.copy(selectedDietType = type, saved = false) }
    }

    fun toggleGoal(goal: String) {
        _state.update { s ->
            val newGoals = if (goal in s.selectedGoals) s.selectedGoals - goal else s.selectedGoals + goal
            // If "Balanced" is picked, clear others; if others picked, remove "Balanced"
            val label = "Balanced / No Restriction"
            val adjusted = if (goal == label) setOf(label) else newGoals - label
            s.copy(selectedGoals = adjusted, saved = false)
        }
    }

    fun save() {
        viewModelScope.launch {
            _state.update { it.copy(saving = true, error = null) }
            try {
                val token = tokenStore.tokenFlow.first().orEmpty()
                val s = _state.value

                // Build the tag string
                val tags = mutableSetOf<String>()

                // Diet type
                when (s.selectedDietType) {
                    "VEG" -> tags.add("VEG")
                    "NON_VEG" -> tags.add("NON_VEG")
                    "MIXED" -> { tags.add("VEG"); tags.add("NON_VEG") }
                }

                // Health goals → expand to individual backend tags
                for (goal in s.selectedGoals) {
                    DietaryOptions.HEALTH_GOALS[goal]?.let { tags.addAll(it) }
                }

                val tagString = tags.joinToString(",")
                val saved = repo.updateDietaryTags(token, tagString)
                _state.update { it.copy(saving = false, saved = true, currentBackendTags = saved) }
            } catch (e: Exception) {
                _state.update { it.copy(saving = false, error = e.message ?: "Failed to save") }
            }
        }
    }
}

