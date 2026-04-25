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
import com.teamfab.meallmatch.person.ui.vm.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMeal: (String) -> Unit,
    onSubscriptions: () -> Unit,
    onProviders: () -> Unit,
    onProfile: () -> Unit,
    vm: HomeViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meals For You") },
                actions = {
                    TextButton(onClick = onProviders) { Text("Providers") }
                    TextButton(onClick = onProfile) { Text("Profile") }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { vm.refresh() },
                    icon = {},
                    label = { Text("Meals") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onSubscriptions,
                    icon = {},
                    label = { Text("Subscriptions") }
                )
            }
        }
    ) { padding ->
        when {
            state.loading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            state.error != null -> Column(Modifier.padding(padding).padding(16.dp)) {
                Text("Error: ${state.error}", color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
                Button(onClick = { vm.refresh() }) { Text("Retry") }
            }

            else -> LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (state.meals.isEmpty()) {
                    item {
                        Column(
                            Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("No compatible meals found.", style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.height(8.dp))
                            Text("Set your dietary preferences in Profile → Diet Preferences",
                                style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                items(state.meals) { meal ->
                    Card(
                        Modifier.fillMaxWidth().clickable { onMeal(meal.id) }
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(meal.title, style = MaterialTheme.typography.titleMedium)
                            meal.providerName?.let {
                                Spacer(Modifier.height(2.dp))
                                Text("By: $it", style = MaterialTheme.typography.bodySmall)
                            }
                            meal.mealType?.takeIf { it.isNotBlank() }?.let {
                                Spacer(Modifier.height(2.dp))
                                Text(it, style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                meal.dietaryTags?.takeIf { it.isNotBlank() } ?: "No dietary info",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(6.dp))
                            Text("₹${meal.price}", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}