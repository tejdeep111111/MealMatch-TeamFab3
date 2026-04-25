package com.teamfab.meallmatch.person.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.teamfab.meallmatch.person.data.model.UpcomingDelivery
import com.teamfab.meallmatch.person.ui.theme.NourishColors
import com.teamfab.meallmatch.person.ui.vm.UpcomingDeliveriesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpcomingDeliveriesScreen(
    vm: UpcomingDeliveriesViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show action errors (skip/unskip failures) as a snackbar
    LaunchedEffect(state.actionError) {
        state.actionError?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearActionError()
        }
    }

    Scaffold(
        containerColor = NourishColors.Background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                color = NourishColors.Surface.copy(alpha = 0.95f),
                tonalElevation = 0.dp,
                modifier = Modifier.border(1.dp, NourishColors.CardBorder, RoundedCornerShape(0.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Upcoming Deliveries",
                            style = MaterialTheme.typography.headlineMedium,
                            color = NourishColors.Primary
                        )
                        Text(
                            "Your next 3 scheduled meals",
                            style = MaterialTheme.typography.bodySmall,
                            color = NourishColors.OnSurfaceVariant
                        )
                    }
                    IconButton(onClick = { vm.load() }) {
                        Icon(
                            Icons.Filled.Refresh,
                            contentDescription = "Refresh",
                            tint = NourishColors.Primary
                        )
                    }
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

            state.error != null -> Column(
                Modifier
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("⚠️", fontSize = 40.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    "Couldn't load deliveries",
                    style = MaterialTheme.typography.titleMedium,
                    color = NourishColors.OnSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    state.error ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = NourishColors.OnSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { vm.load() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NourishColors.PrimaryContainer,
                        contentColor = NourishColors.OnPrimaryContainer
                    )
                ) { Text("Retry") }
            }

            state.upcoming.isEmpty() -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text("📅", fontSize = 48.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "No upcoming deliveries",
                        style = MaterialTheme.typography.titleMedium,
                        color = NourishColors.OnSurface
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Subscribe to a meal plan to see your delivery schedule here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NourishColors.OnSurfaceVariant
                    )
                }
            }

            else -> Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                state.upcoming.forEachIndexed { index, delivery ->
                    UpcomingDeliveryCard(
                        delivery = delivery,
                        position = index,
                        onSkip   = { reason -> vm.skip(delivery, reason) },
                        onUnskip = { vm.unskip(delivery) }
                    )
                }

                // Bottom note
                Spacer(Modifier.height(4.dp))
                Text(
                    "💡 Skipping a delivery keeps your subscription schedule intact — " +
                            "only that specific date is cancelled.",
                    style = MaterialTheme.typography.bodySmall,
                    color = NourishColors.OnSurfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(NourishColors.SurfaceContainer)
                        .padding(12.dp)
                )
            }
        }
    }
}

// ─── Delivery Card ────────────────────────────────────────────────────────────

@Composable
private fun UpcomingDeliveryCard(
    delivery: UpcomingDelivery,
    position: Int,
    onSkip: (reason: String?) -> Unit,
    onUnskip: () -> Unit
) {
    var showConfirmSkip by remember { mutableStateOf(false) }
    var reason by remember { mutableStateOf("") }

    val cardAlpha by animateColorAsState(
        targetValue = if (delivery.isSkipped)
            NourishColors.SurfaceContainerHigh
        else
            NourishColors.SurfaceContainerLowest,
        animationSpec = tween(300),
        label = "card_color"
    )

    // Date label: "Today", "Tomorrow", or day name
    val dateLabel = when (delivery.daysUntil) {
        0    -> "TODAY"
        1    -> "TOMORROW"
        else -> "IN ${delivery.daysUntil} DAYS"
    }

    // Accent color per position (visual variety)
    val accentContainer = when (position) {
        0    -> NourishColors.PrimaryContainer
        1    -> NourishColors.SecondaryContainer
        else -> NourishColors.TertiaryContainer
    }
    val accentOnContainer = when (position) {
        0    -> NourishColors.OnPrimaryContainer
        1    -> NourishColors.OnSecondaryContainer
        else -> NourishColors.OnTertiaryContainer
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = cardAlpha,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(
                    NourishColors.OutlineVariant.copy(alpha = if (delivery.isSkipped) 0.3f else 0.5f),
                    NourishColors.OutlineVariant.copy(alpha = if (delivery.isSkipped) 0.3f else 0.5f)
                )
            )
        ),
        shadowElevation = if (delivery.isSkipped) 0.dp else 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (delivery.isSkipped) 0.75f else 1f)
    ) {
        Column {
            // ── Date header strip ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (delivery.isSkipped)
                            NourishColors.SurfaceVariant
                        else
                            accentContainer.copy(alpha = 0.35f)
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (delivery.isSkipped)
                            NourishColors.SurfaceVariant
                        else
                            accentContainer
                    ) {
                        Text(
                            dateLabel,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = if (delivery.isSkipped)
                                NourishColors.OnSurfaceVariant
                            else
                                accentOnContainer
                        )
                    }
                    Text(
                        delivery.displayDate,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (delivery.isSkipped)
                            NourishColors.OnSurfaceVariant
                        else
                            NourishColors.OnSurface
                    )
                }

                // SKIPPED badge
                AnimatedVisibility(visible = delivery.isSkipped) {
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = NourishColors.ErrorContainer
                    ) {
                        Text(
                            "SKIPPED",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = NourishColors.OnErrorContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // ── Meal details ──
            Column(Modifier.padding(16.dp)) {
                // Emoji + meal name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("🍱", fontSize = 28.sp)
                    Column {
                        Text(
                            delivery.mealName,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (delivery.isSkipped)
                                NourishColors.OnSurfaceVariant
                            else
                                NourishColors.OnSurface,
                            textDecoration = if (delivery.isSkipped)
                                TextDecoration.LineThrough
                            else
                                TextDecoration.None
                        )
                        delivery.providerName?.let {
                            Text(
                                "by $it",
                                style = MaterialTheme.typography.bodySmall,
                                color = NourishColors.OnSurfaceVariant,
                                textDecoration = if (delivery.isSkipped)
                                    TextDecoration.LineThrough
                                else
                                    TextDecoration.None
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Time + address meta
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    delivery.deliveryTime?.takeIf { it.isNotBlank() }?.let {
                        MetaChip(icon = "⏰", text = it)
                    }
                    delivery.deliveryAddress?.takeIf { it.isNotBlank() }?.let {
                        MetaChip(icon = "📍", text = it)
                    }
                }

                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = NourishColors.OutlineVariant.copy(alpha = 0.4f))
                Spacer(Modifier.height(10.dp))

                // ── Action button ──
                if (delivery.isSkipped) {
                    OutlinedButton(
                        onClick = onUnskip,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = NourishColors.Secondary
                        ),
                        border = BorderStroke(1.dp, NourishColors.Secondary)
                    ) {
                        Text(
                            "↩ Restore this delivery",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                } else {
                    if (showConfirmSkip) {
                        // Inline confirmation with optional reason
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NourishColors.ErrorContainer.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(
                                    "Skip delivery on ${delivery.displayDate}?",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = NourishColors.OnErrorContainer,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "Your subscription remains active — only this date is skipped.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NourishColors.OnSurfaceVariant
                                )
                                Spacer(Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = reason,
                                    onValueChange = { reason = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = {
                                        Text(
                                            "Reason (optional)",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = NourishColors.SurfaceContainerLowest,
                                        focusedContainerColor = NourishColors.SurfaceContainerLowest,
                                        unfocusedBorderColor = NourishColors.OutlineVariant.copy(alpha = 0.5f),
                                        focusedBorderColor = NourishColors.Error.copy(alpha = 0.6f)
                                    )
                                )
                                Spacer(Modifier.height(10.dp))
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            showConfirmSkip = false
                                            reason = ""
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Keep it", style = MaterialTheme.typography.labelLarge)
                                    }
                                    Button(
                                        onClick = {
                                            onSkip(reason.takeIf { it.isNotBlank() })
                                            showConfirmSkip = false
                                            reason = ""
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = NourishColors.Error,
                                            contentColor = NourishColors.OnError
                                        )
                                    ) {
                                        Text("Skip", style = MaterialTheme.typography.labelLarge)
                                    }
                                }
                            }
                        }
                    } else {
                        TextButton(
                            onClick = { showConfirmSkip = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "✕  Skip this delivery",
                                style = MaterialTheme.typography.labelLarge,
                                color = NourishColors.Error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetaChip(icon: String, text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = NourishColors.SurfaceContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(icon, fontSize = 12.sp)
            Text(
                text,
                style = MaterialTheme.typography.labelMedium,
                color = NourishColors.OnSurfaceVariant
            )
        }
    }
}






