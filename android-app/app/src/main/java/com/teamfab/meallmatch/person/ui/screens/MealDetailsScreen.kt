package com.teamfab.meallmatch.person.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.teamfab.meallmatch.person.ui.vm.MealDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailsScreen(
    mealId: String,
    onBack: () -> Unit,
    onProvider: (String) -> Unit = {},
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
                    val meal = state.meal!!
                    Text(meal.title, style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(8.dp))
                    meal.providerName?.let { name ->
                        TextButton(
                            onClick = { meal.providerId?.let { onProvider(it) } },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("By: $name  →", style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                    meal.mealType?.takeIf { it.isNotBlank() }?.let {
                        Text("Type: $it", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(4.dp))
                    }
                    meal.dietaryTags?.takeIf { it.isNotBlank() }?.let {
                        Text("Dietary: $it", style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(4.dp))
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Price: ₹${meal.price}", style = MaterialTheme.typography.titleMedium)
                    if (meal.isAvailable == false) {
                        Spacer(Modifier.height(8.dp))
                        Text("Currently unavailable", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}