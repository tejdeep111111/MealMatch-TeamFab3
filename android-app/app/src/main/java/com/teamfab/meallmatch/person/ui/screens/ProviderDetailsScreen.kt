package com.teamfab.meallmatch.person.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.teamfab.meallmatch.person.ui.vm.ProviderDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderDetailsScreen(
    providerId: String,
    onMeal: (String) -> Unit,
    onBack: () -> Unit,
    vm: ProviderDetailsViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    LaunchedEffect(providerId) { vm.load(providerId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Provider Details") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { padding ->
        when {
            state.loading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            state.error != null -> Column(Modifier.padding(padding).padding(16.dp)) {
                Text("Error: ${state.error}", color = MaterialTheme.colorScheme.error)
            }

            state.provider != null -> {
                val provider = state.provider!!
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    /* ── Provider info card ──────────── */
                    item {
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Text(provider.name ?: "Unknown", style = MaterialTheme.typography.headlineSmall)
                                Spacer(Modifier.height(8.dp))
                                provider.cuisineType?.let {
                                    Text("🍽️ Cuisine: $it", style = MaterialTheme.typography.bodyLarge)
                                    Spacer(Modifier.height(4.dp))
                                }
                                provider.location?.let {
                                    Text("📍 Location: $it", style = MaterialTheme.typography.bodyMedium)
                                    Spacer(Modifier.height(4.dp))
                                }
                                provider.phone?.let {
                                    Text("📞 Phone: $it", style = MaterialTheme.typography.bodyMedium)
                                    Spacer(Modifier.height(4.dp))
                                }
                                provider.email?.let {
                                    Text("✉️ Email: $it", style = MaterialTheme.typography.bodyMedium)
                                    Spacer(Modifier.height(4.dp))
                                }
                                provider.rating?.let {
                                    Text("⭐ Rating: $it / 5.0", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }

                    /* ── Menu items heading ──────────── */
                    if (state.meals.isNotEmpty()) {
                        item {
                            Text("Menu Items", style = MaterialTheme.typography.titleMedium)
                        }
                        items(state.meals) { meal ->
                            Card(
                                Modifier.fillMaxWidth().clickable { onMeal(meal.id) }
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(meal.title, style = MaterialTheme.typography.titleSmall)
                                    Spacer(Modifier.height(2.dp))
                                    meal.mealType?.let {
                                        Text(it, style = MaterialTheme.typography.bodySmall)
                                    }
                                    meal.dietaryTags?.let {
                                        Text(it, style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary)
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text("₹${meal.price}", style = MaterialTheme.typography.bodyMedium)
                                    if (meal.isAvailable == false) {
                                        Text("Unavailable", color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }

                    /* ── Reviews heading ─────────────── */
                    if (state.reviews.isNotEmpty()) {
                        item {
                            Spacer(Modifier.height(4.dp))
                            Text("Reviews", style = MaterialTheme.typography.titleMedium)
                        }
                        items(state.reviews) { review ->
                            Card(Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(12.dp)) {
                                    Text("⭐".repeat(review.rating), style = MaterialTheme.typography.bodyLarge)
                                    review.comment?.takeIf { it.isNotBlank() }?.let {
                                        Spacer(Modifier.height(4.dp))
                                        Text(it, style = MaterialTheme.typography.bodyMedium)
                                    }
                                    review.createdAt?.let {
                                        Spacer(Modifier.height(2.dp))
                                        Text(it, style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    } else if (!state.loading) {
                        item { Text("No reviews yet.", style = MaterialTheme.typography.bodySmall) }
                    }
                }
            }
        }
    }
}

