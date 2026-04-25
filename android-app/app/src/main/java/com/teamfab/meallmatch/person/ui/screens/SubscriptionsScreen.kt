package com.teamfab.meallmatch.person.ui.screens

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
import com.teamfab.meallmatch.person.ui.vm.SubscriptionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(
    onBack: () -> Unit,
    vm: SubscriptionsViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Subscriptions") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
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
                Button(onClick = { vm.load() }) { Text("Retry") }
            }

            state.subscriptions.isEmpty() -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No subscriptions yet.")
            }

            else -> LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.subscriptions) { sub ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                sub.menuItemName ?: "Unknown meal",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            sub.providerName?.let {
                                Text("Provider: $it", style = MaterialTheme.typography.bodyMedium)
                                Spacer(Modifier.height(4.dp))
                            }
                            Text("Status: ${sub.status ?: "Unknown"}", style = MaterialTheme.typography.bodyMedium)
                            sub.daysOfWeek?.takeIf { it.isNotBlank() }?.let {
                                Spacer(Modifier.height(4.dp))
                                Text("Days: $it", style = MaterialTheme.typography.bodySmall)
                            }
                            sub.deliveryTime?.takeIf { it.isNotBlank() }?.let {
                                Spacer(Modifier.height(2.dp))
                                Text("Time: $it", style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(Modifier.height(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val status = sub.status?.uppercase()
                                if (status == "ACTIVE") {
                                    OutlinedButton(onClick = { vm.pause(sub.id) }) { Text("Pause") }
                                    OutlinedButton(onClick = { vm.cancel(sub.id) }) { Text("Cancel") }
                                }
                                if (status == "PAUSED") {
                                    OutlinedButton(onClick = { vm.resume(sub.id) }) { Text("Resume") }
                                    OutlinedButton(onClick = { vm.cancel(sub.id) }) { Text("Cancel") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

