package com.teamfab.meallmatch.person.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.teamfab.meallmatch.person.data.model.Meal
import com.teamfab.meallmatch.person.ui.theme.NourishColors
import com.teamfab.meallmatch.person.ui.util.computeMatch
import com.teamfab.meallmatch.person.ui.util.prettyTag
import com.teamfab.meallmatch.person.ui.vm.ProviderDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProviderDetailsScreen(
    providerId: String,
    onMeal: (String) -> Unit,
    onSubscribe: (String) -> Unit = {},
    onBack: () -> Unit,
    vm: ProviderDetailsViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    LaunchedEffect(providerId) { vm.load(providerId) }

    Scaffold(
        containerColor = NourishColors.Background,
        topBar = {
            Surface(color = NourishColors.Surface.copy(alpha = 0.9f), tonalElevation = 0.dp,
                modifier = Modifier.border(width = 1.dp, color = NourishColors.CardBorder, shape = RoundedCornerShape(0.dp))) {
                Row(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    IconButton(onClick = onBack, modifier = Modifier.size(40.dp)
                        .background(NourishColors.SurfaceContainer, CircleShape)
                        .border(1.dp, NourishColors.OutlineVariant.copy(alpha = 0.3f), CircleShape)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = NourishColors.OnSurfaceVariant)
                    }
                    Text("MealMatch", style = MaterialTheme.typography.headlineMedium, color = NourishColors.Primary)
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.Notifications, "Notifications", tint = NourishColors.OnSurfaceVariant)
                    }
                }
            }
        }
    ) { padding ->
        when {
            state.loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NourishColors.PrimaryContainer)
            }
            state.error != null -> Column(Modifier.padding(padding).padding(20.dp)) {
                Text("Error: ${state.error}", color = NourishColors.Error)
            }
            state.provider != null -> {
                val provider = state.provider!!
                LazyColumn(modifier = Modifier.padding(padding), contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)) {

                    // Provider Profile Header (Bento-inspired)
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            // Avatar
                            Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(24.dp))
                                .background(NourishColors.SurfaceContainer)
                                .border(1.dp, NourishColors.OutlineVariant, RoundedCornerShape(24.dp)),
                                contentAlignment = Alignment.Center) {
                                Text("👩‍🍳", fontSize = 40.sp)
                            }
                            // Info
                            Column(modifier = Modifier.weight(1f)) {
                                Text(provider.name ?: "Unknown", style = MaterialTheme.typography.headlineLarge, color = NourishColors.OnSurface)
                                provider.location?.let {
                                    Spacer(Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("📍", fontSize = 14.sp)
                                        Spacer(Modifier.width(4.dp))
                                        Text(it, style = MaterialTheme.typography.bodyMedium, color = NourishColors.OnSurfaceVariant)
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    provider.rating?.let {
                                        Surface(shape = RoundedCornerShape(50), color = NourishColors.SurfaceContainerHigh) {
                                            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Filled.Star, "Rating", tint = NourishColors.PrimaryContainer, modifier = Modifier.size(18.dp))
                                                Spacer(Modifier.width(4.dp))
                                                Text("$it", style = MaterialTheme.typography.labelLarge, color = NourishColors.OnSurface)
                                            }
                                        }
                                    }
                                    Surface(shape = RoundedCornerShape(50), color = NourishColors.SurfaceContainerHigh) {
                                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Text("👥", fontSize = 14.sp)
                                            Spacer(Modifier.width(4.dp))
                                            Text("${state.reviews.size} Reviews", style = MaterialTheme.typography.labelLarge, color = NourishColors.OnSurface)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Provider description
                    item {
                        Surface(shape = RoundedCornerShape(16.dp), color = NourishColors.SurfaceContainerLow,
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NourishColors.OutlineVariant.copy(alpha = 0.5f), NourishColors.OutlineVariant.copy(alpha = 0.5f))))) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                provider.cuisineType?.let {
                                    Text("🍽️ Cuisine: $it", style = MaterialTheme.typography.bodyMedium, color = NourishColors.OnSurfaceVariant)
                                    Spacer(Modifier.height(8.dp))
                                }
                                provider.phone?.let {
                                    Text("📞 $it", style = MaterialTheme.typography.bodyMedium, color = NourishColors.OnSurfaceVariant)
                                    Spacer(Modifier.height(4.dp))
                                }
                                provider.email?.let {
                                    Text("✉️ $it", style = MaterialTheme.typography.bodyMedium, color = NourishColors.OnSurfaceVariant)
                                }
                            }
                        }
                    }

                    // Available Dishes header
                    item {
                        Text("Available Dishes", style = MaterialTheme.typography.titleLarge, color = NourishColors.OnSurface)
                    }

                    if (state.meals.isEmpty()) {
                        item {
                            Text("No meals listed by this provider yet.", style = MaterialTheme.typography.bodyMedium, color = NourishColors.OnSurfaceVariant)
                        }
                    }

                    // 2-column grid of meal cards (matching provider_profile stitch)
                    // Since we're inside LazyColumn, we use chunked approach
                    val chunkedMeals = state.meals.chunked(2)
                    items(chunkedMeals) { rowMeals ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            rowMeals.forEach { meal ->
                                ProviderMealCard(meal = meal, userTags = state.userTags,
                                    onMeal = { onMeal(meal.id) }, onSubscribe = { onSubscribe(meal.id) },
                                    modifier = Modifier.weight(1f))
                            }
                            // Fill empty space if odd number
                            if (rowMeals.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }

                    // View All button
                    if (state.meals.size > 4) {
                        item {
                            Button(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NourishColors.SurfaceContainerHigh, contentColor = NourishColors.OnSurface),
                                contentPadding = PaddingValues(vertical = 16.dp),
                                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NourishColors.OutlineVariant.copy(alpha = 0.5f), NourishColors.OutlineVariant.copy(alpha = 0.5f))))) {
                                Text("VIEW ALL DISHES", style = MaterialTheme.typography.labelLarge)
                                Spacer(Modifier.width(8.dp))
                                Text("→", fontSize = 16.sp)
                            }
                        }
                    }

                    // Reviews section
                    if (state.reviews.isNotEmpty()) {
                        item { Text("Reviews (${state.reviews.size})", style = MaterialTheme.typography.titleLarge, color = NourishColors.OnSurface) }
                        items(state.reviews) { review ->
                            Surface(shape = RoundedCornerShape(16.dp), color = NourishColors.SurfaceContainerLowest,
                                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NourishColors.OutlineVariant, NourishColors.OutlineVariant)))) {
                                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        repeat(review.rating) {
                                            Icon(Icons.Filled.Star, "Star", tint = NourishColors.PrimaryContainer, modifier = Modifier.size(16.dp))
                                        }
                                        repeat(5 - review.rating) {
                                            Icon(Icons.Filled.Star, "Star", tint = NourishColors.SurfaceVariant, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    review.comment?.takeIf { it.isNotBlank() }?.let {
                                        Spacer(Modifier.height(8.dp))
                                        Text(it, style = MaterialTheme.typography.bodyMedium, color = NourishColors.OnSurface)
                                    }
                                    review.createdAt?.let {
                                        Spacer(Modifier.height(4.dp))
                                        Text(it, style = MaterialTheme.typography.bodySmall, color = NourishColors.OnSurfaceVariant)
                                    }
                                }
                            }
                        }
                    } else if (!state.loading) {
                        item { Text("No reviews yet.", style = MaterialTheme.typography.bodySmall, color = NourishColors.OnSurfaceVariant) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProviderMealCard(meal: Meal, userTags: List<String>, onMeal: () -> Unit, onSubscribe: () -> Unit, modifier: Modifier = Modifier) {
    val match = computeMatch(meal, userTags)
    Card(modifier = modifier.clickable { onMeal() }, shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NourishColors.SurfaceContainerLowest),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NourishColors.OutlineVariant, NourishColors.OutlineVariant)))) {
        Column {
            // Image area
            Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f).background(NourishColors.SurfaceContainerHigh),
                contentAlignment = Alignment.Center) {
                Text(when (meal.mealType?.uppercase()) { "BREAKFAST" -> "🌅"; "LUNCH" -> "☀️"; "DINNER" -> "🌙"; "SNACK" -> "🍪"; else -> "🍽️" }, fontSize = 36.sp)
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(meal.title, style = MaterialTheme.typography.headlineSmall.copy(fontSize = 18.sp), color = NourishColors.OnSurface,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(8.dp))
                // Diet tag chips
                if (userTags.isNotEmpty() && match.matchedTags.isNotEmpty()) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        match.matchedTags.take(2).forEach { tag ->
                            Surface(shape = RoundedCornerShape(50), color = NourishColors.SecondaryContainer.copy(alpha = 0.6f)) {
                                Text(prettyTag(tag).uppercase(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall, color = NourishColors.OnSecondaryContainer)
                            }
                        }
                    }
                } else {
                    meal.mealType?.let { type ->
                        Surface(shape = RoundedCornerShape(50), color = NourishColors.SurfaceVariant) {
                            Text(type.uppercase(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall, color = NourishColors.OnSurfaceVariant)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text("₹${meal.price}", style = MaterialTheme.typography.titleMedium, color = NourishColors.Primary)
                if (meal.isAvailable == false) {
                    Text("Unavailable", style = MaterialTheme.typography.labelSmall, color = NourishColors.Error)
                }
            }
        }
    }
}
