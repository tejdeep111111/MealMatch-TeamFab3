package com.teamfab.meallmatch.person.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.teamfab.meallmatch.person.ui.theme.NourishColors
import com.teamfab.meallmatch.person.ui.vm.SubscribeMealViewModel
import java.text.SimpleDateFormat
import java.util.*

private val ALL_DAYS = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
private val TIME_SLOTS = listOf("08:00", "09:00", "10:00", "12:00", "12:30", "13:00", "14:00", "19:00", "20:00")

private fun milliToDateString(millis: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date(millis))
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SubscribeMealScreen(
    mealId: String,
    onBack: () -> Unit,
    onSubscribed: () -> Unit,
    vm: SubscribeMealViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    LaunchedEffect(mealId) { vm.loadMeal(mealId) }
    LaunchedEffect(state.success) { if (state.success != null) onSubscribed() }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val startDatePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    val endDatePickerState = rememberDatePickerState()

    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = { TextButton(onClick = { startDatePickerState.selectedDateMillis?.let { vm.setStartDate(milliToDateString(it)) }; showStartDatePicker = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = startDatePickerState) }
    }
    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = { TextButton(onClick = { endDatePickerState.selectedDateMillis?.let { vm.setEndDate(milliToDateString(it)) }; showEndDatePicker = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { vm.setEndDate(""); showEndDatePicker = false }) { Text("Clear") } }
        ) { DatePicker(state = endDatePickerState) }
    }

    Scaffold(
        containerColor = NourishColors.Background,
        topBar = {
            Surface(color = NourishColors.Surface.copy(alpha = 0.9f), tonalElevation = 0.dp,
                modifier = Modifier.border(1.dp, NourishColors.CardBorder, RoundedCornerShape(0.dp))) {
                Row(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(40.dp)
                        .background(NourishColors.SurfaceContainer, CircleShape)
                        .border(1.dp, NourishColors.OutlineVariant.copy(alpha = 0.3f), CircleShape)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = NourishColors.OnSurfaceVariant)
                    }
                    Spacer(Modifier.width(16.dp))
                    Text("Subscribe to Meal", style = MaterialTheme.typography.headlineMedium, color = NourishColors.Primary)
                }
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).verticalScroll(rememberScrollState())) {
            when {
                state.loading -> Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NourishColors.PrimaryContainer)
                }
                state.meal == null -> Text("Meal not found", color = NourishColors.Error, modifier = Modifier.padding(20.dp))
                else -> {
                    val meal = state.meal!!
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        // Meal info card
                        Surface(shape = RoundedCornerShape(16.dp), color = NourishColors.SurfaceContainerLow,
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NourishColors.OutlineVariant, NourishColors.OutlineVariant)))) {
                            Column(Modifier.fillMaxWidth().padding(20.dp)) {
                                Text(meal.title, style = MaterialTheme.typography.headlineLarge, color = NourishColors.OnSurface)
                                meal.providerName?.let {
                                    Spacer(Modifier.height(4.dp))
                                    Text("By $it", style = MaterialTheme.typography.bodyMedium, color = NourishColors.OnSurfaceVariant)
                                }
                                Spacer(Modifier.height(8.dp))
                                Text("₹${meal.price} / delivery", style = MaterialTheme.typography.titleLarge, color = NourishColors.Primary)
                            }
                        }

                        // Days of week
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Delivery Days", style = MaterialTheme.typography.titleLarge, color = NourishColors.OnSurface)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ALL_DAYS.forEach { day ->
                                    val isSelected = day in state.selectedDays
                                    Box(modifier = Modifier.size(44.dp).clip(CircleShape)
                                        .background(if (isSelected) NourishColors.PrimaryContainer else NourishColors.SurfaceContainerHigh, CircleShape)
                                        .border(if (isSelected) 2.dp else 1.dp, if (isSelected) NourishColors.PrimaryContainer else NourishColors.OutlineVariant, CircleShape)
                                        .clickable { vm.toggleDay(day) },
                                        contentAlignment = Alignment.Center) {
                                        Text(day.take(1), style = MaterialTheme.typography.labelLarge,
                                            color = if (isSelected) NourishColors.OnPrimaryContainer else NourishColors.OnSurfaceVariant)
                                    }
                                }
                            }
                        }

                        // Delivery time
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Delivery Time", style = MaterialTheme.typography.titleLarge, color = NourishColors.OnSurface)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                TIME_SLOTS.forEach { time ->
                                    val isSelected = state.deliveryTime == time
                                    Surface(shape = RoundedCornerShape(50),
                                        color = if (isSelected) NourishColors.PrimaryContainer else NourishColors.SurfaceContainerLowest,
                                        border = CardDefaults.outlinedCardBorder().copy(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            brush = Brush.linearGradient(listOf(
                                                if (isSelected) NourishColors.PrimaryContainer else NourishColors.OutlineVariant,
                                                if (isSelected) NourishColors.PrimaryContainer else NourishColors.OutlineVariant
                                            ))
                                        ),
                                        modifier = Modifier.clickable { vm.setDeliveryTime(time) }) {
                                        Text(time, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (isSelected) NourishColors.OnPrimaryContainer else NourishColors.OnSurfaceVariant)
                                    }
                                }
                            }
                        }

                        // Address
                        OutlinedTextField(value = state.deliveryAddress, onValueChange = { vm.setDeliveryAddress(it) },
                            label = { Text("Delivery Address") }, modifier = Modifier.fillMaxWidth(), minLines = 2,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NourishColors.PrimaryContainer,
                                unfocusedBorderColor = NourishColors.OutlineVariant,
                                focusedLabelColor = NourishColors.Primary
                            ))

                        // Dates
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Start Date", style = MaterialTheme.typography.titleMedium, color = NourishColors.OnSurface)
                                Spacer(Modifier.height(8.dp))
                                OutlinedButton(onClick = { showStartDatePicker = true }, modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NourishColors.OnSurface)) {
                                    Text(if (state.startDate.isNotBlank()) "📅 ${state.startDate}" else "📅 Pick date")
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("End Date", style = MaterialTheme.typography.titleMedium, color = NourishColors.OnSurface)
                                Spacer(Modifier.height(8.dp))
                                OutlinedButton(onClick = { showEndDatePicker = true }, modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NourishColors.OnSurface)) {
                                    Text(if (state.endDate.isNotBlank()) "📅 ${state.endDate}" else "📅 Optional")
                                }
                            }
                        }

                        // Subscribe button
                        Button(onClick = { vm.subscribe() },
                            enabled = !state.submitting && state.selectedDays.isNotEmpty() && state.startDate.isNotBlank() && state.deliveryAddress.isNotBlank(),
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NourishColors.PrimaryContainer, contentColor = NourishColors.OnPrimaryContainer),
                            contentPadding = PaddingValues(vertical = 16.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)) {
                            Text(if (state.submitting) "SUBSCRIBING…" else "SUBSCRIBE NOW", style = MaterialTheme.typography.labelLarge)
                        }

                        state.error?.let {
                            Surface(shape = RoundedCornerShape(16.dp), color = NourishColors.ErrorContainer) {
                                Text(it, modifier = Modifier.padding(16.dp), color = NourishColors.OnErrorContainer)
                            }
                        }
                    }
                }
            }
        }
    }
}
