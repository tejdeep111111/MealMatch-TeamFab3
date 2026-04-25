package com.teamfab.meallmatch.person.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.teamfab.meallmatch.person.data.model.SubscriptionResponse
import com.teamfab.meallmatch.person.ui.theme.NourishColors
import com.teamfab.meallmatch.person.ui.vm.SubscriptionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(
    onBack: () -> Unit,
    vm: SubscriptionsViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    Scaffold(
        containerColor = NourishColors.Background,
        topBar = {
            Surface(color = NourishColors.Surface.copy(alpha = 0.9f), tonalElevation = 0.dp,
                modifier = Modifier.border(width = 1.dp, color = NourishColors.CardBorder, shape = RoundedCornerShape(0.dp))) {
                Row(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = NourishColors.OnSurfaceVariant)
                    }
                    Text("My Subscriptions", style = MaterialTheme.typography.headlineMedium, color = NourishColors.Primary)
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.Notifications, "Notifications", tint = NourishColors.Primary)
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
                Spacer(Modifier.height(8.dp))
                Button(onClick = { vm.load() }, colors = ButtonDefaults.buttonColors(containerColor = NourishColors.PrimaryContainer, contentColor = NourishColors.OnPrimaryContainer)) { Text("Retry") }
            }
            state.subscriptions.isEmpty() -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📋", fontSize = 48.sp)
                    Spacer(Modifier.height(16.dp))
                    Text("No subscriptions yet.", style = MaterialTheme.typography.bodyLarge, color = NourishColors.OnSurfaceVariant)
                }
            }
            else -> {
                val active = state.subscriptions.filter { it.status?.uppercase() == "ACTIVE" }
                val paused = state.subscriptions.filter { it.status?.uppercase() == "PAUSED" }
                val other = state.subscriptions.filter { it.status?.uppercase() != "ACTIVE" && it.status?.uppercase() != "PAUSED" }

                LazyColumn(modifier = Modifier.padding(padding), contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    // Active section
                    if (active.isNotEmpty()) {
                        item { Text("Active", style = MaterialTheme.typography.titleLarge, color = NourishColors.OnSurface) }
                        items(active) { sub -> SubscriptionCard(sub = sub, isActive = true, onPause = { vm.pause(sub.id) }, onCancel = { vm.cancel(sub.id) }) }
                    }
                    // Paused section
                    if (paused.isNotEmpty()) {
                        item { Text("Paused", style = MaterialTheme.typography.titleLarge, color = NourishColors.OnSurfaceVariant.copy(alpha = 0.8f)) }
                        items(paused) { sub -> SubscriptionCard(sub = sub, isActive = false, onResume = { vm.resume(sub.id) }, onCancel = { vm.cancel(sub.id) }) }
                    }
                    // Other statuses
                    if (other.isNotEmpty()) {
                        item { Text("Other", style = MaterialTheme.typography.titleLarge, color = NourishColors.OnSurfaceVariant) }
                        items(other) { sub -> SubscriptionCard(sub = sub, isActive = false, onCancel = { vm.cancel(sub.id) }) }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubscriptionCard(
    sub: SubscriptionResponse,
    isActive: Boolean,
    onPause: (() -> Unit)? = null,
    onResume: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null
) {
    val alpha = if (isActive) 1f else 0.8f
    Card(modifier = Modifier.fillMaxWidth().alpha(alpha), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isActive) NourishColors.SurfaceContainerLowest else NourishColors.SurfaceContainer),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(
            if (isActive) NourishColors.OutlineVariant else NourishColors.OutlineVariant.copy(alpha = 0.5f),
            if (isActive) NourishColors.OutlineVariant else NourishColors.OutlineVariant.copy(alpha = 0.5f)
        )))) {
        Column {
            // Image placeholder area with status badge
            Box(modifier = Modifier.fillMaxWidth().height(128.dp).background(
                if (isActive) NourishColors.SurfaceContainerHigh else NourishColors.SurfaceVariant)) {
                Text("🍱", fontSize = 40.sp, modifier = Modifier.align(Alignment.Center))
                // Status badge
                Surface(modifier = Modifier.align(Alignment.TopEnd).padding(12.dp), shape = RoundedCornerShape(50),
                    color = if (isActive) NourishColors.SecondaryContainer else NourishColors.SurfaceDim) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(
                            if (isActive) NourishColors.Secondary else NourishColors.Outline))
                        Spacer(Modifier.width(6.dp))
                        Text(sub.status?.replaceFirstChar { it.uppercase() } ?: "Unknown",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isActive) NourishColors.OnSecondaryContainer else NourishColors.OnSurfaceVariant)
                    }
                }
            }
            // Card content
            Column(modifier = Modifier.padding(16.dp)) {
                sub.providerName?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium, color = NourishColors.OnSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                }
                Text(sub.menuItemName ?: "Unknown meal", style = MaterialTheme.typography.headlineMedium, color = NourishColors.OnSurface,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)

                // Day indicators
                sub.daysOfWeek?.takeIf { it.isNotBlank() }?.let { daysStr ->
                    Spacer(Modifier.height(12.dp))
                    val selectedDays = daysStr.split(",").map { it.trim().uppercase() }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("M" to "MON", "T" to "TUE", "W" to "WED", "T" to "THU", "F" to "FRI", "S" to "SAT", "S" to "SUN").forEach { (label, day) ->
                            val isDayActive = selectedDays.contains(day)
                            Box(modifier = Modifier.size(28.dp).clip(CircleShape)
                                .background(if (isDayActive && isActive) NourishColors.PrimaryContainer
                                    else if (isDayActive) NourishColors.SurfaceVariant
                                    else NourishColors.SurfaceVariant),
                                contentAlignment = Alignment.Center) {
                                Text(label, style = MaterialTheme.typography.labelSmall,
                                    color = if (isDayActive && isActive) NourishColors.OnPrimaryContainer else NourishColors.OnSurfaceVariant)
                            }
                        }
                    }
                }

                sub.deliveryTime?.takeIf { it.isNotBlank() }?.let {
                    Spacer(Modifier.height(8.dp))
                    Text("⏰ $it", style = MaterialTheme.typography.bodySmall, color = NourishColors.OnSurfaceVariant)
                }

                // Action buttons
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = NourishColors.SurfaceVariant)
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    if (isActive && onPause != null) {
                        TextButton(onClick = onPause) {
                            Text("PAUSE", style = MaterialTheme.typography.labelLarge, color = NourishColors.OnSurfaceVariant)
                        }
                    }
                    if (!isActive && onResume != null) {
                        TextButton(onClick = onResume) {
                            Text("RESUME", style = MaterialTheme.typography.labelLarge, color = NourishColors.Primary)
                        }
                    }
                    if (onCancel != null) {
                        TextButton(onClick = onCancel) {
                            Text("CANCEL", style = MaterialTheme.typography.labelLarge, color = NourishColors.Error)
                        }
                    }
                }
            }
        }
    }
}
