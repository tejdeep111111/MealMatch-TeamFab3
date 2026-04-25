package com.teamfab.meallmatch.person.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.teamfab.meallmatch.person.ui.vm.ProvidersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProvidersScreen(
    onProvider: (String) -> Unit,
    onBack: () -> Unit,
    vm: ProvidersViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meal Providers") },
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
                Spacer(Modifier.height(8.dp))
                Button(onClick = { vm.load() }) { Text("Retry") }
            }

            state.providers.isEmpty() -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text("No providers found.") }

            else -> LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.providers) { provider ->
                    Card(
                        Modifier.fillMaxWidth().clickable { onProvider(provider.id) }
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text(
                                provider.name ?: "Unknown",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            provider.cuisineType?.let {
                                Text("Cuisine: $it", style = MaterialTheme.typography.bodyMedium)
                                Spacer(Modifier.height(2.dp))
                            }
                            provider.location?.let {
                                Text("📍 $it", style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.height(2.dp))
                            }
                            provider.rating?.let {
                                Text("⭐ $it", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

