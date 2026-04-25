package com.teamfab.meallmatch.person.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.teamfab.meallmatch.person.ui.theme.NourishColors
import com.teamfab.meallmatch.person.ui.util.prettyTag
import com.teamfab.meallmatch.person.ui.vm.WeekDaySlot
import com.teamfab.meallmatch.person.ui.vm.WeeklySummaryViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WeeklySummaryScreen(
    onBack: () -> Unit,
    vm: WeeklySummaryViewModel = hiltViewModel()
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = NourishColors.Primary
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Weekly Summary",
                            style = MaterialTheme.typography.headlineMedium,
                            color = NourishColors.Primary
                        )
                        if (state.weekLabel.isNotBlank()) {
                            Text(
                                state.weekLabel,
                                style = MaterialTheme.typography.bodySmall,
                                color = NourishColors.OnSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = { vm.load() }) {
                        Icon(Icons.Filled.Refresh, "Refresh", tint = NourishColors.Primary)
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
                Modifier.fillMaxSize().padding(padding).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("⚠️", fontSize = 40.sp)
                Spacer(Modifier.height(12.dp))
                Text(state.error ?: "", style = MaterialTheme.typography.bodyMedium,
                    color = NourishColors.OnSurfaceVariant, textAlign = TextAlign.Center)
                Spacer(Modifier.height(16.dp))
                Button(onClick = { vm.load() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NourishColors.PrimaryContainer,
                        contentColor = NourishColors.OnPrimaryContainer
                    )
                ) { Text("Retry") }
            }

            else -> Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // ── 1. Week Calendar Strip ────────────────────────────────
                SectionCard(title = "This Week's Schedule") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        state.weekDays.forEach { slot ->
                            WeekDayCell(slot = slot, modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    // Legend
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LegendDot(NourishColors.SecondaryContainer, "Delivery")
                        LegendDot(NourishColors.ErrorContainer, "Skipped")
                        LegendDot(NourishColors.SurfaceContainerHigh, "No delivery")
                    }
                }

                // ── 2. At-a-glance stats ──────────────────────────────────
                SectionCard(title = "At a Glance") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GlanceStatCard(
                            emoji = "📦",
                            value = state.totalDeliveriesThisWeek.toString(),
                            label = "Deliveries\nthis week",
                            color = NourishColors.PrimaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                        GlanceStatCard(
                            emoji = "✂️",
                            value = state.skippedThisWeek.toString(),
                            label = "Skipped\nthis week",
                            color = if (state.skippedThisWeek > 0) NourishColors.ErrorContainer
                                    else NourishColors.SurfaceContainerHigh,
                            modifier = Modifier.weight(1f)
                        )
                        GlanceStatCard(
                            emoji = "🍽️",
                            value = state.compatibleMealsCount.toString(),
                            label = "Matched\nmeals",
                            color = NourishColors.SecondaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                        GlanceStatCard(
                            emoji = "📋",
                            value = state.subscriptions.size.toString(),
                            label = "Active\nplans",
                            color = NourishColors.TertiaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // ── 3. Diet Profile ───────────────────────────────────────
                SectionCard(title = "My Diet Profile") {
                    if (state.activeTags.isEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("⚙️", fontSize = 24.sp)
                            Text(
                                "No dietary preferences set yet.\nGo to Profile → Diet Preferences to set them.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NourishColors.OnSurfaceVariant
                            )
                        }
                    } else {
                        // Diet type badge
                        state.dietType?.let { type ->
                            val (emoji, label) = when (type) {
                                "VEG"     -> "🥬" to "Vegetarian"
                                "NON_VEG" -> "🍗" to "Non-Vegetarian"
                                "MIXED"   -> "🍽️" to "Mixed Diet"
                                else      -> "🥗" to type
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(emoji, fontSize = 20.sp)
                                Text(
                                    label,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = NourishColors.OnSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                        }

                        // Health goals
                        if (state.healthGoals.isNotEmpty()) {
                            Text(
                                "Goals",
                                style = MaterialTheme.typography.labelLarge,
                                color = NourishColors.OnSurfaceVariant
                            )
                            Spacer(Modifier.height(4.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                state.healthGoals.forEach { goal ->
                                    GoalChip(label = goal)
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                        }

                        // All active tags
                        Text(
                            "Active dietary tags",
                            style = MaterialTheme.typography.labelLarge,
                            color = NourishColors.OnSurfaceVariant
                        )
                        Spacer(Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            state.activeTags.forEach { tag ->
                                TagChip(tag = tag)
                            }
                        }
                    }
                }

                // ── 4. Active Subscriptions ───────────────────────────────
                if (state.subscriptions.isNotEmpty()) {
                    SectionCard(title = "Active Meal Plans") {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            state.subscriptions.forEach { sub ->
                                SubMiniCard(
                                    mealName = sub.menuItemName ?: "Unknown meal",
                                    providerName = sub.providerName,
                                    daysOfWeek = sub.daysOfWeek,
                                    deliveryTime = sub.deliveryTime
                                )
                            }
                        }
                    }
                }

                // ── 5. This week's day-by-day detail ─────────────────────
                val deliveryDays = state.weekDays.filter { it.hasDelivery }
                if (deliveryDays.isNotEmpty()) {
                    SectionCard(title = "Day-by-Day") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            deliveryDays.forEach { slot ->
                                DayDetailRow(slot)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ─── Sub-composables ─────────────────────────────────────────────────────────

@Composable
private fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = NourishColors.SurfaceContainerLowest,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(
                    NourishColors.OutlineVariant.copy(alpha = 0.4f),
                    NourishColors.OutlineVariant.copy(alpha = 0.4f)
                )
            )
        ),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = NourishColors.Primary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun WeekDayCell(slot: WeekDaySlot, modifier: Modifier = Modifier) {
    val circleColor = when {
        slot.isFullySkipped  -> NourishColors.ErrorContainer
        slot.isPartlySkipped -> NourishColors.PrimaryContainer.copy(alpha = 0.5f)
        slot.hasDelivery     -> NourishColors.SecondaryContainer
        else                 -> NourishColors.SurfaceContainerHigh
    }
    val textColor = when {
        slot.isFullySkipped  -> NourishColors.OnErrorContainer
        slot.hasDelivery     -> NourishColors.OnSecondaryContainer
        else                 -> NourishColors.OnSurfaceVariant
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Day letter
        Text(
            slot.dayAbbrev,
            style = MaterialTheme.typography.labelSmall,
            color = if (slot.isToday) NourishColors.Primary else NourishColors.OnSurfaceVariant,
            fontWeight = if (slot.isToday) FontWeight.Bold else FontWeight.Normal
        )
        // Date circle
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(circleColor)
                .then(
                    if (slot.isToday) Modifier.border(2.dp, NourishColors.Primary, CircleShape)
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                slot.dateNum,
                style = MaterialTheme.typography.labelMedium,
                color = textColor,
                fontWeight = if (slot.isToday) FontWeight.Bold else FontWeight.Normal
            )
        }
        // Meal count dot
        if (slot.hasDelivery) {
            Text(
                if (slot.deliveries.size == 1) "🍱" else "×${slot.deliveries.size}",
                style = MaterialTheme.typography.labelSmall,
                color = NourishColors.OnSurfaceVariant
            )
        } else {
            Spacer(Modifier.height(14.dp))
        }
    }
}

@Composable
private fun GlanceStatCard(
    emoji: String,
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = color.copy(alpha = 0.35f)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 20.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                color = NourishColors.OnSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = NourishColors.OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun GoalChip(label: String) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = NourishColors.PrimaryContainer.copy(alpha = 0.3f)
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelMedium,
            color = NourishColors.OnPrimaryContainer
        )
    }
}

@Composable
private fun TagChip(tag: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = NourishColors.SecondaryContainer.copy(alpha = 0.35f)
    ) {
        Text(
            prettyTag(tag),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = NourishColors.Secondary
        )
    }
}

@Composable
private fun SubMiniCard(
    mealName: String,
    providerName: String?,
    daysOfWeek: String?,
    deliveryTime: String?
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = NourishColors.SurfaceContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("🍱", fontSize = 24.sp)
            Column(Modifier.weight(1f)) {
                Text(
                    mealName,
                    style = MaterialTheme.typography.titleSmall,
                    color = NourishColors.OnSurface
                )
                providerName?.let {
                    Text(
                        "by $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = NourishColors.OnSurfaceVariant
                    )
                }
                // Day pills
                daysOfWeek?.takeIf { it.isNotBlank() }?.let { days ->
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        days.split(",").map { it.trim().take(3) }.take(7).forEach { dayAbbr ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = NourishColors.PrimaryContainer.copy(alpha = 0.4f)
                            ) {
                                Text(
                                    dayAbbr,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NourishColors.OnPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
            deliveryTime?.takeIf { it.isNotBlank() }?.let {
                Text(
                    "⏰ $it",
                    style = MaterialTheme.typography.labelSmall,
                    color = NourishColors.OnSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DayDetailRow(slot: WeekDaySlot) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Day label
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(44.dp)
        ) {
            Text(
                slot.dayName,
                style = MaterialTheme.typography.labelMedium,
                color = if (slot.isToday) NourishColors.Primary else NourishColors.OnSurfaceVariant,
                fontWeight = if (slot.isToday) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                slot.dateNum,
                style = MaterialTheme.typography.labelSmall,
                color = NourishColors.OnSurfaceVariant
            )
        }
        // Meals
        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            slot.deliveries.forEachIndexed { idx, name ->
                val isThisSkipped = idx < slot.skippedCount
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isThisSkipped) NourishColors.ErrorContainer.copy(alpha = 0.3f)
                            else NourishColors.SecondaryContainer.copy(alpha = 0.25f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(if (isThisSkipped) "✂️" else "✅", fontSize = 14.sp)
                        Text(
                            name,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isThisSkipped) NourishColors.OnSurfaceVariant
                                    else NourishColors.OnSurface
                        )
                        if (isThisSkipped) {
                            Spacer(Modifier.weight(1f))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = NourishColors.ErrorContainer
                            ) {
                                Text(
                                    "Skipped",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NourishColors.OnErrorContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: androidx.compose.ui.graphics.Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(label, style = MaterialTheme.typography.labelSmall, color = NourishColors.OnSurfaceVariant)
    }
}

