package com.teamfab.meallmatch.person.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.teamfab.meallmatch.person.ui.vm.MealDetailsViewModel

@Composable
fun MealDetailsScreen(
    mealId: String,
    onBack: () -> Unit,
    vm: MealDetailsViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    LaunchedEffect(mealId) { vm.load(mealId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meal Details") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            when {
                state.loading -> CircularProgressIndicator()
                state.error != null -> Text("Error: ${state.error}", color = MaterialTheme.colorScheme.error)
                state.meal != null -> {
                    Text(state.meal!!.title, style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(8.dp))
                    Text(state.meal!!.description)
                    Spacer(Modifier.height(12.dp))
                    Text("Price: ₹${state.meal!!.price}", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}