package com.teamfab.meallmatch.person.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.teamfab.meallmatch.person.ui.theme.NourishColors
import com.teamfab.meallmatch.person.ui.util.computeMatch
import com.teamfab.meallmatch.person.ui.util.prettyTag
import com.teamfab.meallmatch.person.ui.vm.MealDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MealDetailsScreen(
    mealId: String,
    onBack: () -> Unit,
    onProvider: (String) -> Unit = {},
    onSubscribe: (String) -> Unit = {},
    vm: MealDetailsViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    LaunchedEffect(mealId) { vm.load(mealId) }

    Box(modifier = Modifier.fillMaxSize().background(NourishColors.Background)) {
        when {
            state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NourishColors.PrimaryContainer)
            }
            state.error != null -> Column(Modifier.padding(20.dp).padding(top = 60.dp)) {
                Text("Error: ${state.error}", color = NourishColors.Error)
            }
            state.meal != null -> {
                val meal = state.meal!!
                val match = computeMatch(meal, state.userTags)

                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    // Hero Image area
                    Box(modifier = Modifier.fillMaxWidth().height(353.dp).background(NourishColors.SurfaceContainerHighest)) {
                        // Meal type emoji centered
                        Text(
                            when (meal.mealType?.uppercase()) { "BREAKFAST" -> "🌅"; "LUNCH" -> "☀️"; "DINNER" -> "🌙"; "SNACK" -> "🍪"; else -> "🍽️" },
                            fontSize = 72.sp, modifier = Modifier.align(Alignment.Center)
                        )
                        // Bottom gradient overlay
                        Box(
                            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(128.dp)
                                .background(Brush.verticalGradient(listOf(Color.Transparent, NourishColors.Background)))
                        )
                    }

                    // Content container (bottom sheet style)
                    Surface(
                        modifier = Modifier.fillMaxWidth().offset(y = (-24).dp),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        color = NourishColors.Surface,
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            // Title + Rating
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                                Text(meal.title, style = MaterialTheme.typography.headlineLarge, color = NourishColors.OnSurface, modifier = Modifier.weight(1f))
                                if (state.userTags.isNotEmpty() && match.matchPercent > 0) {
                                    Surface(shape = RoundedCornerShape(12.dp), color = NourishColors.SurfaceContainerHigh) {
                                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Filled.Star, "Rating", tint = NourishColors.PrimaryContainer, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("${match.matchPercent}%", style = MaterialTheme.typography.labelLarge, color = NourishColors.OnSurfaceVariant)
                                        }
                                    }
                                }
                            }

                            // Provider name
                            meal.providerName?.let { name ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🏪", fontSize = 14.sp)
                                    Spacer(Modifier.width(8.dp))
                                    TextButton(onClick = { meal.providerId?.let { onProvider(it) } }, contentPadding = PaddingValues(0.dp)) {
                                        Text("By $name", style = MaterialTheme.typography.bodyMedium, color = NourishColors.OnSurfaceVariant)
                                    }
                                }
                            }

                            // Diet Compatibility Tags
                            if (state.userTags.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("Diet Compatibility", style = MaterialTheme.typography.titleLarge, color = NourishColors.OnSurface)
                                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        match.matchedTags.forEach { tag ->
                                            Surface(shape = RoundedCornerShape(50), color = NourishColors.SecondaryContainer,
                                                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NourishColors.SecondaryContainer.copy(alpha = 0.5f), NourishColors.SecondaryContainer.copy(alpha = 0.5f))))) {
                                                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                                    Text("✓", fontSize = 14.sp, color = NourishColors.OnSecondaryContainer)
                                                    Spacer(Modifier.width(6.dp))
                                                    Text(prettyTag(tag), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium), color = NourishColors.OnSecondaryContainer)
                                                }
                                            }
                                        }
                                        // Show unmatched user tags as tertiary
                                        val unmatchedTags = state.userTags.filter { it !in match.matchedTags }
                                        unmatchedTags.forEach { tag ->
                                            Surface(shape = RoundedCornerShape(50), color = NourishColors.SurfaceVariant,
                                                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NourishColors.OutlineVariant, NourishColors.OutlineVariant)))) {
                                                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                                    Text("—", fontSize = 14.sp, color = NourishColors.OnSurfaceVariant)
                                                    Spacer(Modifier.width(6.dp))
                                                    Text(prettyTag(tag), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium), color = NourishColors.OnSurfaceVariant)
                                                }
                                            }
                                        }
                                    }
                                    // Match summary card
                                    Surface(shape = RoundedCornerShape(16.dp),
                                        color = when { match.matchPercent >= 75 -> NourishColors.SecondaryContainer.copy(alpha = 0.3f); match.matchPercent >= 50 -> NourishColors.PrimaryContainer.copy(alpha = 0.2f); else -> NourishColors.SurfaceContainer }) {
                                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Text(match.emoji, fontSize = 24.sp)
                                            Spacer(Modifier.width(12.dp))
                                            Column {
                                                Text("${match.matchPercent}% — ${match.label}", style = MaterialTheme.typography.titleSmall, color = NourishColors.OnSurface)
                                                if (match.matchedTags.isNotEmpty()) {
                                                    Text("Matches ${match.matchedTags.size} of ${state.userTags.size} dietary preferences",
                                                        style = MaterialTheme.typography.bodySmall, color = NourishColors.OnSurfaceVariant)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Meal type info card
                            meal.mealType?.takeIf { it.isNotBlank() }?.let { mealType ->
                                Surface(shape = RoundedCornerShape(16.dp), color = NourishColors.SurfaceContainerLow,
                                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NourishColors.OutlineVariant.copy(alpha = 0.3f), NourishColors.OutlineVariant.copy(alpha = 0.3f))))) {
                                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Text("Meal Info", style = MaterialTheme.typography.titleLarge, color = NourishColors.OnSurface)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(NourishColors.PrimaryContainer))
                                            Spacer(Modifier.width(8.dp))
                                            Text("Type: $mealType", style = MaterialTheme.typography.bodyMedium, color = NourishColors.OnSurfaceVariant)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(NourishColors.PrimaryContainer))
                                            Spacer(Modifier.width(8.dp))
                                            Text("Price: ₹${meal.price}", style = MaterialTheme.typography.bodyMedium, color = NourishColors.OnSurfaceVariant)
                                        }
                                    }
                                }
                            }

                            // Availability status
                            if (meal.isAvailable == false) {
                                Surface(shape = RoundedCornerShape(16.dp), color = NourishColors.ErrorContainer) {
                                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text("⚠️", fontSize = 20.sp)
                                        Spacer(Modifier.width(12.dp))
                                        Text("Currently unavailable", style = MaterialTheme.typography.titleSmall, color = NourishColors.OnErrorContainer)
                                    }
                                }
                            }

                            // Spacer for bottom bar
                            Spacer(Modifier.height(80.dp))
                        }
                    }
                }

                // Top navigation overlay
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp).statusBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(NourishColors.SurfaceContainerLowest.copy(alpha = 0.8f), CircleShape)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = NourishColors.OnSurface)
                    }
                    IconButton(onClick = {}, modifier = Modifier.size(40.dp).background(NourishColors.SurfaceContainerLowest.copy(alpha = 0.8f), CircleShape)) {
                        Icon(Icons.Filled.Favorite, "Favorite", tint = NourishColors.PrimaryContainer)
                    }
                }

                // Sticky bottom action bar
                if (meal.isAvailable != false) {
                    Surface(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                        color = NourishColors.SurfaceContainerLowest.copy(alpha = 0.9f), shadowElevation = 8.dp,
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NourishColors.OutlineVariant.copy(alpha = 0.3f), NourishColors.OutlineVariant.copy(alpha = 0.3f))))) {
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("Price", style = MaterialTheme.typography.bodyMedium, color = NourishColors.OnSurfaceVariant)
                                Text("₹${meal.price}", style = MaterialTheme.typography.headlineMedium, color = NourishColors.OnSurface)
                            }
                            Button(onClick = { onSubscribe(meal.id) }, shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NourishColors.PrimaryContainer, contentColor = NourishColors.OnPrimaryContainer),
                                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)) {
                                Text("Subscribe", style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp))
                            }
                        }
                    }
                }
            }
        }
    }
}