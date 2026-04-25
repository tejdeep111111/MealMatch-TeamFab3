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

@Composable
fun HomeScreen(
    onMeal: (String) -> Unit,
    onOrders: () -> Unit,
    onProfile: () -> Unit,
    vm: HomeViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meals") },
                actions = {
                    TextButton(onClick = onOrders) { Text("Orders") }
                    TextButton(onClick = onProfile) { Text("Profile") }
                }
            )
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
                items(state.meals) { meal ->
                    Card(
                        Modifier.fillMaxWidth().clickable { onMeal(meal.id) }
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(meal.title, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(meal.description, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(6.dp))
                            Text("₹${meal.price}", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}