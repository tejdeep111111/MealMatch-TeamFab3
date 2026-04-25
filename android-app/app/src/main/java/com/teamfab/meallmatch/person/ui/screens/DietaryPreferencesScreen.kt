package com.teamfab.meallmatch.person.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.teamfab.meallmatch.person.ui.vm.DietaryOptions
import com.teamfab.meallmatch.person.ui.vm.DietaryPreferencesViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DietaryPreferencesScreen(
    onBack: () -> Unit,
    vm: DietaryPreferencesViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Diet Preferences") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { padding ->
        when {
            state.loading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            else -> Column(
                Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                /* ── Section 1 : Diet type ─────────────── */
                Text("Diet Type", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DietaryOptions.DIET_TYPES.forEach { type ->
                        val label = when (type) {
                            "VEG" -> "🥬 Veg"
                            "NON_VEG" -> "🍗 Non-Veg"
                            "MIXED" -> "🍽️ Mixed"
                            else -> type
                        }
                        FilterChip(
                            selected = state.selectedDietType == type,
                            onClick = { vm.setDietType(type) },
                            label = { Text(label) }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                /* ── Section 2 : Health goals ──────────── */
                Text("Health Goal / Condition", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DietaryOptions.HEALTH_GOALS.keys.forEach { goal ->
                        val emoji = when {
                            goal.contains("Diabetic") -> "🩺"
                            goal.contains("Hypertension") -> "❤️‍🩹"
                            goal.contains("Fat Loss") -> "🏃"
                            goal.contains("Muscle") -> "💪"
                            goal.contains("Fiber") -> "🌾"
                            goal.contains("Balanced") -> "⚖️"
                            else -> ""
                        }
                        FilterChip(
                            selected = goal in state.selectedGoals,
                            onClick = { vm.toggleGoal(goal) },
                            label = { Text("$emoji $goal") }
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                /* ── Preview of tags ─────────────────── */
                if (state.selectedDietType != null || state.selectedGoals.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Your preferences", style = MaterialTheme.typography.labelLarge)
                            Spacer(Modifier.height(4.dp))
                            state.selectedDietType?.let {
                                Text("Diet: $it", style = MaterialTheme.typography.bodyMedium)
                            }
                            if (state.selectedGoals.isNotEmpty()) {
                                Text("Goals: ${state.selectedGoals.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                /* ── Save button ─────────────────────── */
                Button(
                    onClick = { vm.save() },
                    enabled = !state.saving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (state.saving) "Saving…" else "Save Preferences")
                }

                if (state.saved) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "✅ Preferences saved! Meals will now be personalised for you.",
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                state.error?.let {
                    Spacer(Modifier.height(8.dp))
                    Text("Error: $it", color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

