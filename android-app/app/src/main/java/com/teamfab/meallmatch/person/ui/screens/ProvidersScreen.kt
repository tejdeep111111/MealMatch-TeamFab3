package com.teamfab.meallmatch.person.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.teamfab.meallmatch.person.ui.theme.NourishColors
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
        containerColor = NourishColors.Background,
        topBar = {
            Surface(color = NourishColors.Surface.copy(alpha = 0.9f), tonalElevation = 0.dp,
                modifier = Modifier.border(1.dp, NourishColors.CardBorder, RoundedCornerShape(0.dp))) {
                Row(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(NourishColors.SurfaceContainer, CircleShape)
                        .border(1.dp, NourishColors.OutlineVariant.copy(alpha = 0.3f), CircleShape)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = NourishColors.OnSurfaceVariant)
                    }
                    Spacer(Modifier.width(16.dp))
                    Text("Meal Providers", style = MaterialTheme.typography.headlineMedium, color = NourishColors.Primary)
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
                Spacer(Modifier.height(8.dp))
                Button(onClick = { vm.load() }, colors = ButtonDefaults.buttonColors(containerColor = NourishColors.PrimaryContainer, contentColor = NourishColors.OnPrimaryContainer)) { Text("Retry") }
            }
            state.providers.isEmpty() -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("👩‍🍳", fontSize = 48.sp)
                    Spacer(Modifier.height(16.dp))
                    Text("No providers found.", style = MaterialTheme.typography.bodyLarge, color = NourishColors.OnSurfaceVariant)
                }
            }
            else -> LazyColumn(modifier = Modifier.padding(padding), contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(state.providers) { provider ->
                    Surface(modifier = Modifier.fillMaxWidth().clickable { onProvider(provider.id) },
                        shape = RoundedCornerShape(16.dp), color = NourishColors.SurfaceContainerLowest,
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NourishColors.OutlineVariant, NourishColors.OutlineVariant)))) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(64.dp).clip(RoundedCornerShape(16.dp)).background(NourishColors.SurfaceContainer)
                                .border(1.dp, NourishColors.OutlineVariant, RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center) {
                                Text("👩‍🍳", fontSize = 28.sp)
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(provider.name ?: "Unknown", style = MaterialTheme.typography.headlineSmall, color = NourishColors.OnSurface)
                                provider.cuisineType?.let {
                                    Spacer(Modifier.height(4.dp))
                                    Text("🍽️ $it", style = MaterialTheme.typography.bodyMedium, color = NourishColors.OnSurfaceVariant)
                                }
                                provider.location?.let {
                                    Spacer(Modifier.height(2.dp))
                                    Text("📍 $it", style = MaterialTheme.typography.bodySmall, color = NourishColors.OnSurfaceVariant)
                                }
                            }
                            provider.rating?.let {
                                Surface(shape = RoundedCornerShape(50), color = NourishColors.SurfaceContainerHigh) {
                                    Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Star, "Rating", tint = NourishColors.PrimaryContainer, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("$it", style = MaterialTheme.typography.labelLarge, color = NourishColors.OnSurface)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
