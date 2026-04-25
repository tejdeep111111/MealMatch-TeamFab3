package com.teamfab.meallmatch.person.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.teamfab.meallmatch.person.ui.theme.NourishColors
import com.teamfab.meallmatch.person.ui.vm.DietaryOptions
import com.teamfab.meallmatch.person.ui.vm.DietaryPreferencesViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DietaryPreferencesScreen(
    onBack: () -> Unit,
    vm: DietaryPreferencesViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    Scaffold(containerColor = NourishColors.Background) { padding ->
        when {
            state.loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NourishColors.PrimaryContainer)
            }
            else -> Column(Modifier.fillMaxSize().padding(padding)) {
                // Progress Header (matching onboarding stitch)
                Row(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(40.dp)
                        .background(NourishColors.SurfaceContainerHigh, CircleShape)) {
                        Icon(Icons.Filled.Close, "Close", tint = NourishColors.OnSurfaceVariant)
                    }
                    Spacer(Modifier.width(12.dp))
                    // Progress bar
                    Box(modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(50))
                        .background(NourishColors.SurfaceVariant)) {
                        val progress = when {
                            state.selectedDietType != null && state.selectedGoals.isNotEmpty() -> 1f
                            state.selectedDietType != null || state.selectedGoals.isNotEmpty() -> 0.5f
                            else -> 0.15f
                        }
                        Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(progress).clip(RoundedCornerShape(50))
                            .background(NourishColors.PrimaryContainer))
                    }
                }

                Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
                    // Title
                    Text("What's your\nprimary goal?", style = MaterialTheme.typography.displayLarge, color = NourishColors.OnBackground, lineHeight = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("We'll tailor your meal recommendations based on what you want to achieve.",
                        style = MaterialTheme.typography.bodyLarge, color = NourishColors.OnSurfaceVariant)
                    Spacer(Modifier.height(24.dp))

                    // Diet Type section
                    Text("Diet Type", style = MaterialTheme.typography.titleLarge, color = NourishColors.OnSurface)
                    Spacer(Modifier.height(12.dp))
                    DietaryOptions.DIET_TYPES.forEach { type ->
                        val isSelected = state.selectedDietType == type
                        val (emoji, label, subtitle) = when (type) {
                            "VEG" -> Triple("🥬", "Vegetarian", "Plant-based meals only")
                            "NON_VEG" -> Triple("🍗", "Non-Vegetarian", "Includes meat & fish")
                            "MIXED" -> Triple("🍽️", "Mixed", "Everything goes")
                            else -> Triple("", type, "")
                        }
                        GoalCard(emoji = emoji, title = label, subtitle = subtitle, isSelected = isSelected,
                            iconBgColor = NourishColors.SecondaryContainer,
                            iconContentColor = NourishColors.OnSecondaryContainer,
                            onClick = { vm.setDietType(type) })
                        Spacer(Modifier.height(12.dp))
                    }

                    Spacer(Modifier.height(24.dp))

                    // Health goals section
                    Text("Health Goal", style = MaterialTheme.typography.titleLarge, color = NourishColors.OnSurface)
                    Spacer(Modifier.height(12.dp))
                    DietaryOptions.HEALTH_GOALS.keys.forEach { goal ->
                        val isSelected = goal in state.selectedGoals
                        val (emoji, iconBg, iconContent) = when {
                            goal.contains("Diabetic") -> Triple("💧", NourishColors.SecondaryContainer, NourishColors.OnSecondaryContainer)
                            goal.contains("Hypertension") -> Triple("❤️", NourishColors.ErrorContainer, NourishColors.OnErrorContainer)
                            goal.contains("Fat Loss") -> Triple("🔥", NourishColors.TertiaryContainer, NourishColors.OnTertiaryContainer)
                            goal.contains("Muscle") -> Triple("💪", NourishColors.SurfaceVariant, NourishColors.OnSurfaceVariant)
                            goal.contains("Fiber") -> Triple("🌾", NourishColors.SecondaryContainer, NourishColors.OnSecondaryContainer)
                            goal.contains("Balanced") -> Triple("⚖️", NourishColors.SurfaceContainerHigh, NourishColors.OnSurfaceVariant)
                            else -> Triple("", NourishColors.SurfaceVariant, NourishColors.OnSurfaceVariant)
                        }
                        val subtitle = when {
                            goal.contains("Diabetic") -> "Low glycemic index meals"
                            goal.contains("Hypertension") -> "Heart-healthy, low salt"
                            goal.contains("Fat Loss") -> "Calorie controlled, high protein"
                            goal.contains("Muscle") -> "High protein, calorie surplus"
                            goal.contains("Fiber") -> "High fiber content meals"
                            goal.contains("Balanced") -> "No specific restrictions"
                            else -> ""
                        }
                        GoalCard(emoji = emoji, title = goal, subtitle = subtitle, isSelected = isSelected,
                            iconBgColor = iconBg, iconContentColor = iconContent,
                            onClick = { vm.toggleGoal(goal) })
                        Spacer(Modifier.height(12.dp))
                    }

                    // Preview card
                    if (state.selectedDietType != null || state.selectedGoals.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Surface(shape = RoundedCornerShape(16.dp), color = NourishColors.SurfaceContainerLow,
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NourishColors.OutlineVariant, NourishColors.OutlineVariant)))) {
                            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                                Text("Your preferences", style = MaterialTheme.typography.labelLarge, color = NourishColors.OnSurfaceVariant)
                                Spacer(Modifier.height(8.dp))
                                state.selectedDietType?.let { Text("Diet: $it", style = MaterialTheme.typography.bodyMedium, color = NourishColors.OnSurface) }
                                if (state.selectedGoals.isNotEmpty()) {
                                    Text("Goals: ${state.selectedGoals.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium, color = NourishColors.OnSurface)
                                }
                            }
                        }
                    }

                    if (state.saved) {
                        Spacer(Modifier.height(12.dp))
                        Surface(shape = RoundedCornerShape(16.dp), color = NourishColors.SecondaryContainer.copy(alpha = 0.3f)) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("✅", fontSize = 20.sp)
                                Spacer(Modifier.width(12.dp))
                                Text("Preferences saved! Meals will now be personalised for you.",
                                    style = MaterialTheme.typography.bodyMedium, color = NourishColors.Secondary)
                            }
                        }
                    }
                    state.error?.let {
                        Spacer(Modifier.height(12.dp))
                        Surface(shape = RoundedCornerShape(16.dp), color = NourishColors.ErrorContainer) {
                            Text("Error: $it", modifier = Modifier.padding(16.dp), color = NourishColors.OnErrorContainer)
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }

                // Sticky bottom CTA
                Surface(color = NourishColors.Background, tonalElevation = 0.dp) {
                    Button(onClick = { vm.save() }, enabled = !state.saving,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NourishColors.PrimaryContainer, contentColor = NourishColors.OnPrimaryContainer),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)) {
                        Text(if (state.saving) "SAVING…" else "CONTINUE", style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.width(8.dp))
                        Text("→", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalCard(
    emoji: String, title: String, subtitle: String, isSelected: Boolean,
    iconBgColor: Color, iconContentColor: Color, onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) NourishColors.SurfaceContainerLow else NourishColors.SurfaceContainerLowest,
        border = CardDefaults.outlinedCardBorder().copy(
            width = if (isSelected) 2.dp else 1.dp,
            brush = Brush.linearGradient(listOf(
                if (isSelected) NourishColors.PrimaryContainer else NourishColors.OutlineVariant,
                if (isSelected) NourishColors.PrimaryContainer else NourishColors.OutlineVariant
            ))
        ),
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            // Icon circle
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(iconBgColor),
                contentAlignment = Alignment.Center) {
                Text(emoji, fontSize = 24.sp)
            }
            Spacer(Modifier.width(24.dp))
            // Text
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleLarge, color = NourishColors.OnSurface)
                if (subtitle.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = NourishColors.OnSurfaceVariant)
                }
            }
            Spacer(Modifier.width(12.dp))
            // Selection indicator
            Box(modifier = Modifier.size(24.dp).clip(CircleShape)
                .border(2.dp, if (isSelected) NourishColors.PrimaryContainer else NourishColors.OutlineVariant, CircleShape)
                .then(if (isSelected) Modifier.background(NourishColors.PrimaryContainer, CircleShape) else Modifier),
                contentAlignment = Alignment.Center) {
                if (isSelected) {
                    Icon(Icons.Filled.Check, "Selected", tint = NourishColors.OnPrimaryContainer, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
