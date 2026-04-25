package com.teamfab.meallmatch.person.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.teamfab.meallmatch.person.data.model.Meal
import com.teamfab.meallmatch.person.ui.theme.NourishColors
import com.teamfab.meallmatch.person.ui.util.MatchInfo
import com.teamfab.meallmatch.person.ui.util.computeMatch
import com.teamfab.meallmatch.person.ui.util.prettyTag
import com.teamfab.meallmatch.person.ui.vm.HomeViewModel
import com.teamfab.meallmatch.person.ui.vm.PriceSort

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    onMeal: (String) -> Unit,
    onSubscriptions: () -> Unit = {},
    onProviders: () -> Unit = {},
    onProfile: () -> Unit = {},
    vm: HomeViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    Scaffold(
        containerColor = NourishColors.Background,
        topBar = {
            Surface(
                color = NourishColors.Surface.copy(alpha = 0.95f),
                tonalElevation = 0.dp,
                modifier = Modifier.border(1.dp, NourishColors.CardBorder, RoundedCornerShape(0.dp))
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Meals For You", style = MaterialTheme.typography.headlineMedium, color = NourishColors.Primary)

                    Spacer(Modifier.height(10.dp))

                    // ── Search Bar ──
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { vm.updateSearch(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search meals, providers, tags…", style = MaterialTheme.typography.bodyMedium) },
                        leadingIcon = { Icon(Icons.Filled.Search, "Search", tint = NourishColors.OnSurfaceVariant) },
                        trailingIcon = {
                            if (state.searchQuery.isNotBlank()) {
                                IconButton(onClick = { vm.updateSearch("") }) {
                                    Icon(Icons.Filled.Close, "Clear", tint = NourishColors.OnSurfaceVariant)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = NourishColors.SurfaceContainerLowest,
                            focusedContainerColor = NourishColors.SurfaceContainerLowest,
                            unfocusedBorderColor = NourishColors.OutlineVariant.copy(alpha = 0.5f),
                            focusedBorderColor = NourishColors.PrimaryContainer
                        )
                    )

                    Spacer(Modifier.height(10.dp))

                    // ── Category Chips ──
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.availableCategories) { cat ->
                            val selected = cat == state.selectedCategory
                            FilterChip(
                                selected = selected,
                                onClick = { vm.selectCategory(cat) },
                                label = { Text(cat, style = MaterialTheme.typography.labelLarge) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NourishColors.PrimaryContainer,
                                    selectedLabelColor = NourishColors.OnPrimaryContainer,
                                    containerColor = NourishColors.SurfaceContainerLowest,
                                    labelColor = NourishColors.OnSurfaceVariant
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = NourishColors.OutlineVariant.copy(alpha = 0.5f),
                                    selectedBorderColor = NourishColors.PrimaryContainer,
                                    enabled = true,
                                    selected = selected
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // ── Filter Row: Compatible toggle + Price sort ──
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Compatible toggle
                        if (state.userTags.isNotEmpty()) {
                            FilterChip(
                                selected = state.showOnlyCompatible,
                                onClick = { vm.toggleFilter() },
                                label = {
                                    Text(
                                        if (state.showOnlyCompatible) "✅ My Diet" else "All Meals",
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NourishColors.SecondaryContainer,
                                    selectedLabelColor = NourishColors.OnSecondaryContainer,
                                    containerColor = NourishColors.SurfaceContainerLowest,
                                    labelColor = NourishColors.OnSurfaceVariant
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = NourishColors.OutlineVariant.copy(alpha = 0.5f),
                                    selectedBorderColor = NourishColors.SecondaryContainer,
                                    enabled = true,
                                    selected = state.showOnlyCompatible
                                )
                            )
                        }

                        // Price sort chip
                        val priceSortLabel = when (state.priceSort) {
                            PriceSort.NONE -> "Price"
                            PriceSort.LOW_TO_HIGH -> "Price ↑"
                            PriceSort.HIGH_TO_LOW -> "Price ↓"
                        }
                        FilterChip(
                            selected = state.priceSort != PriceSort.NONE,
                            onClick = { vm.cyclePriceSort() },
                            label = { Text(priceSortLabel, style = MaterialTheme.typography.labelLarge) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NourishColors.TertiaryContainer,
                                selectedLabelColor = NourishColors.OnTertiaryContainer,
                                containerColor = NourishColors.SurfaceContainerLowest,
                                labelColor = NourishColors.OnSurfaceVariant
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = NourishColors.OutlineVariant.copy(alpha = 0.5f),
                                selectedBorderColor = NourishColors.TertiaryContainer,
                                enabled = true,
                                selected = state.priceSort != PriceSort.NONE
                            )
                        )

                        Spacer(Modifier.weight(1f))

                        // Result count
                        Text(
                            "${state.meals.size} of ${state.allMeals.size}",
                            style = MaterialTheme.typography.labelMedium,
                            color = NourishColors.OnSurfaceVariant
                        )
                    }

                    Spacer(Modifier.height(4.dp))
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

            state.error != null -> Column(Modifier.padding(padding).padding(16.dp)) {
                Text("Error: ${state.error}", color = NourishColors.Error)
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { vm.refresh() },
                    colors = ButtonDefaults.buttonColors(containerColor = NourishColors.PrimaryContainer)
                ) { Text("Retry") }
            }

            else -> LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.meals.isEmpty()) {
                    item {
                        Column(
                            Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("😔", fontSize = 40.sp)
                            Spacer(Modifier.height(12.dp))
                            Text(
                                if (state.searchQuery.isNotBlank()) "No meals match your search."
                                else if (state.showOnlyCompatible) "No compatible meals found."
                                else "No meals available.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = NourishColors.OnSurface
                            )
                            Spacer(Modifier.height(8.dp))
                            if (state.showOnlyCompatible && state.searchQuery.isBlank()) {
                                TextButton(onClick = { vm.toggleFilter() }) {
                                    Text("Show all meals instead", color = NourishColors.Primary)
                                }
                            }
                            if (state.searchQuery.isNotBlank()) {
                                TextButton(onClick = { vm.updateSearch("") }) {
                                    Text("Clear search", color = NourishColors.Primary)
                                }
                            }
                        }
                    }
                }

                items(state.meals, key = { it.id }) { meal ->
                    val match = remember(meal.id, state.userTags) { computeMatch(meal, state.userTags) }
                    MealCard(meal = meal, match = match, userTags = state.userTags, onClick = { onMeal(meal.id) })
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MealCard(
    meal: Meal,
    match: MatchInfo,
    userTags: List<String>,
    onClick: () -> Unit
) {
    // Stable border brush — created once per card composition, not on every recompose
    val borderBrush = remember {
        Brush.linearGradient(
            listOf(
                NourishColors.OutlineVariant.copy(alpha = 0.4f),
                NourishColors.OutlineVariant.copy(alpha = 0.4f)
            )
        )
    }
    // Parse dietary tags once per card, not on every recompose
    val parsedTags = remember(meal.dietaryTags) {
        meal.dietaryTags?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }?.take(5)
            ?: emptyList()
    }

    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = NourishColors.SurfaceContainerLowest,
        border = CardDefaults.outlinedCardBorder().copy(brush = borderBrush),
        shadowElevation = 1.dp
    ) {
        Column(Modifier.padding(16.dp)) {
            // Title row + match badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(meal.title, style = MaterialTheme.typography.titleMedium, color = NourishColors.OnSurface)
                    meal.providerName?.let {
                        Spacer(Modifier.height(2.dp))
                        Text("by $it", style = MaterialTheme.typography.bodySmall, color = NourishColors.OnSurfaceVariant)
                    }
                }
                // Match badge
                if (userTags.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when {
                                    match.matchPercent >= 75 -> NourishColors.SecondaryContainer
                                    match.matchPercent >= 50 -> NourishColors.PrimaryContainer.copy(alpha = 0.5f)
                                    match.matchPercent > 0  -> NourishColors.SurfaceContainerHigh
                                    else                    -> NourishColors.SurfaceContainerHigh.copy(alpha = 0.5f)
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "${match.emoji} ${match.matchPercent}%",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                match.matchPercent >= 75 -> NourishColors.OnSecondaryContainer
                                match.matchPercent >= 50 -> NourishColors.OnPrimaryContainer
                                else                     -> NourishColors.OnSurfaceVariant
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Meal type + price row
            Row(verticalAlignment = Alignment.CenterVertically) {
                meal.mealType?.takeIf { it.isNotBlank() }?.let {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = NourishColors.TertiaryContainer.copy(alpha = 0.3f)
                    ) {
                        Text(
                            it,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = NourishColors.Tertiary
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    "₹${meal.price}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NourishColors.Primary
                )
            }

            // Dietary tags
            if (parsedTags.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    parsedTags.forEach { tag ->
                        val isMatched = tag in userTags
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isMatched) NourishColors.SecondaryContainer.copy(alpha = 0.4f)
                                    else NourishColors.SurfaceContainer
                        ) {
                            Text(
                                prettyTag(tag),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isMatched) NourishColors.Secondary else NourishColors.OnSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}