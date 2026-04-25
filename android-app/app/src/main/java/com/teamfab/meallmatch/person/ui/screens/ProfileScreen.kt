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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.teamfab.meallmatch.person.ui.theme.NourishColors
import com.teamfab.meallmatch.person.ui.vm.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit = {},
    onDietaryPreferences: () -> Unit = {},
    onWeeklySummary: () -> Unit = {},
    onLogout: () -> Unit,
    vm: ProfileViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    Scaffold(
        containerColor = NourishColors.Background,
        topBar = {
            Surface(
                color = NourishColors.Surface.copy(alpha = 0.9f),
                tonalElevation = 0.dp,
                modifier = Modifier.border(1.dp, NourishColors.CardBorder, RoundedCornerShape(0.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Profile", style = MaterialTheme.typography.headlineMedium, color = NourishColors.Primary)
                }
            }
        }
    ) { padding ->
        when {
            state.loading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = NourishColors.PrimaryContainer)
            }

            else -> Column(
                Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))

                // ── Profile Avatar with Initials ──
                val initials = state.email
                    .substringBefore("@")
                    .split(".", "_", "-")
                    .take(2)
                    .joinToString("") { it.take(1).uppercase() }
                    .ifEmpty { "?" }

                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(NourishColors.PrimaryContainer, NourishColors.SecondaryContainer)
                            )
                        )
                        .border(3.dp, NourishColors.PrimaryContainer.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        initials,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = NourishColors.OnPrimaryContainer
                    )
                }

                Spacer(Modifier.height(16.dp))

                // ── Email ──
                if (state.email.isNotBlank()) {
                    Text(
                        state.email,
                        style = MaterialTheme.typography.titleMedium,
                        color = NourishColors.OnSurface
                    )
                    Spacer(Modifier.height(4.dp))
                }

                // ── Dietary Tags Badge ──
                if (state.dietaryTags.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = NourishColors.SecondaryContainer.copy(alpha = 0.4f)
                    ) {
                        Text(
                            state.dietaryTags.split(",").joinToString(" · ") { it.trim() },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = NourishColors.Secondary
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = NourishColors.SurfaceContainerHigh
                    ) {
                        Text(
                            "No dietary preferences set",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = NourishColors.OnSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ── Stats Row ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        emoji = "📋",
                        value = state.activeSubscriptions.toString(),
                        label = "Active Subs",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        emoji = "⭐",
                        value = state.totalReviews.toString(),
                        label = "Reviews",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        emoji = "🍽️",
                        value = state.compatibleMeals.toString(),
                        label = "Matched Meals",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(28.dp))

                // ── Settings Section ──
                Text(
                    "Settings",
                    style = MaterialTheme.typography.titleLarge,
                    color = NourishColors.OnSurface,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // Diet Preferences
                ProfileMenuItem(
                    emoji = "🥗",
                    title = "Diet Preferences",
                    subtitle = "Manage your dietary goals & restrictions",
                    onClick = onDietaryPreferences
                )

                Spacer(Modifier.height(12.dp))

                // Weekly Summary
                ProfileMenuItem(
                    emoji = "📊",
                    title = "Weekly Summary",
                    subtitle = "Your meal schedule, preferences & stats this week",
                    onClick = onWeeklySummary
                )

                Spacer(Modifier.height(12.dp))

                // About section
                ProfileMenuItem(
                    emoji = "ℹ️",
                    title = "About MealMatch",
                    subtitle = "Version 1.0 · Made with ❤️ by Team Fab",
                    onClick = { }
                )

                Spacer(Modifier.height(32.dp))

                // ── Sign Out ──
                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NourishColors.ErrorContainer.copy(alpha = 0.3f),
                        contentColor = NourishColors.Error
                    ),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        "Sign Out",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("SIGN OUT", style = MaterialTheme.typography.labelLarge)
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun StatCard(
    emoji: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = NourishColors.SurfaceContainerLowest,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(NourishColors.OutlineVariant.copy(alpha = 0.5f), NourishColors.OutlineVariant.copy(alpha = 0.5f))
            )
        ),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 24.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                color = NourishColors.Primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = NourishColors.OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    emoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = NourishColors.SurfaceContainerLowest,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(NourishColors.OutlineVariant.copy(alpha = 0.5f), NourishColors.OutlineVariant.copy(alpha = 0.5f))
            )
        ),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(NourishColors.SurfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 22.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = NourishColors.OnSurface)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = NourishColors.OnSurfaceVariant)
            }
            Text("→", fontSize = 20.sp, color = NourishColors.OnSurfaceVariant)
        }
    }
}