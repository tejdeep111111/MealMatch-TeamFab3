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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teamfab.meallmatch.person.ui.theme.NourishColors
import com.teamfab.meallmatch.person.ui.vm.AuthViewModel
import com.teamfab.meallmatch.person.ui.vm.DietaryOptions

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LoginScreen(
    vm: AuthViewModel,
    onLoggedIn: () -> Unit
) {
    val state by vm.state.collectAsState()

    var isRegister by remember { mutableStateOf(false) }
    var registerStep by remember { mutableIntStateOf(1) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    var selectedDietType by remember { mutableStateOf<String?>(null) }
    var selectedGoals by remember { mutableStateOf(emptySet<String>()) }

    LaunchedEffect(state.token, state.loggedOut) {
        if (!state.token.isNullOrBlank() && !state.loggedOut) onLoggedIn()
    }

    Box(modifier = Modifier.fillMaxSize().background(NourishColors.Background).statusBarsPadding()) {
        Column(
            Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            if (!isRegister) {
                // LOGIN
                Text("🍽️", fontSize = 48.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(Modifier.height(12.dp))
                Text("MealMatch", style = MaterialTheme.typography.displayLarge, color = NourishColors.Primary, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(Modifier.height(4.dp))
                Text("Sign in to your account", style = MaterialTheme.typography.bodyLarge, color = NourishColors.OnSurfaceVariant, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(Modifier.height(32.dp))

                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NourishColors.PrimaryContainer, unfocusedBorderColor = NourishColors.OutlineVariant, focusedLabelColor = NourishColors.Primary))
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NourishColors.PrimaryContainer, unfocusedBorderColor = NourishColors.OutlineVariant, focusedLabelColor = NourishColors.Primary))
                Spacer(Modifier.height(20.dp))

                Button(onClick = { vm.login(email, password) }, enabled = !state.loading, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = NourishColors.PrimaryContainer, contentColor = NourishColors.OnPrimaryContainer),
                    contentPadding = PaddingValues(vertical = 16.dp), elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)) {
                    Text(if (state.loading) "SIGNING IN…" else "SIGN IN", style = MaterialTheme.typography.labelLarge)
                }
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = { isRegister = true; registerStep = 1 }, modifier = Modifier.fillMaxWidth()) {
                    Text("Don't have an account? Register", color = NourishColors.Primary)
                }

            } else if (registerStep == 1) {
                // REGISTER Step 1
                Text("Create Account", style = MaterialTheme.typography.displayLarge, color = NourishColors.OnBackground)
                Spacer(Modifier.height(4.dp))
                Text("Step 1 of 2 – Your details", style = MaterialTheme.typography.bodyMedium, color = NourishColors.OnSurfaceVariant)
                Spacer(Modifier.height(24.dp))

                val textFieldColors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NourishColors.PrimaryContainer, unfocusedBorderColor = NourishColors.OutlineVariant, focusedLabelColor = NourishColors.Primary)
                val textFieldShape = RoundedCornerShape(16.dp)
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = textFieldShape, colors = textFieldColors)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = textFieldShape, colors = textFieldColors)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), singleLine = true, shape = textFieldShape, colors = textFieldColors)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone (optional)") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = textFieldShape, colors = textFieldColors)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Delivery Location") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = textFieldShape, colors = textFieldColors)
                Spacer(Modifier.height(24.dp))

                Button(onClick = { registerStep = 2 }, enabled = name.isNotBlank() && email.isNotBlank() && password.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NourishColors.PrimaryContainer, contentColor = NourishColors.OnPrimaryContainer),
                    contentPadding = PaddingValues(vertical = 16.dp)) {
                    Text("NEXT → CHOOSE PREFERENCES", style = MaterialTheme.typography.labelLarge)
                }
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { isRegister = false }, modifier = Modifier.fillMaxWidth()) {
                    Text("Already have an account? Sign in", color = NourishColors.Primary)
                }

            } else {
                // REGISTER Step 2 - Dietary Preferences
                Text("Your Food\nPreferences", style = MaterialTheme.typography.displayLarge, color = NourishColors.OnBackground, lineHeight = 48.sp)
                Spacer(Modifier.height(4.dp))
                Text("Step 2 of 2 – Personalise your meals", style = MaterialTheme.typography.bodyMedium, color = NourishColors.OnSurfaceVariant)
                Spacer(Modifier.height(24.dp))

                Text("Diet Type", style = MaterialTheme.typography.titleLarge, color = NourishColors.OnSurface)
                Spacer(Modifier.height(12.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DietaryOptions.DIET_TYPES.forEach { type ->
                        val (emoji, label) = when (type) { "VEG" -> "🥬" to "Veg"; "NON_VEG" -> "🍗" to "Non-Veg"; "MIXED" -> "🍽️" to "Mixed"; else -> "" to type }
                        val isSelected = selectedDietType == type
                        Surface(shape = RoundedCornerShape(50),
                            color = if (isSelected) NourishColors.PrimaryContainer else NourishColors.SurfaceContainerLowest,
                            border = CardDefaults.outlinedCardBorder().copy(width = if (isSelected) 2.dp else 1.dp,
                                brush = Brush.linearGradient(listOf(if (isSelected) NourishColors.PrimaryContainer else NourishColors.OutlineVariant, if (isSelected) NourishColors.PrimaryContainer else NourishColors.OutlineVariant))),
                            modifier = Modifier.clickable { selectedDietType = type }) {
                            Text("$emoji $label", modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp), style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) NourishColors.OnPrimaryContainer else NourishColors.OnSurfaceVariant)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
                Text("Health Goal / Condition", style = MaterialTheme.typography.titleLarge, color = NourishColors.OnSurface)
                Spacer(Modifier.height(12.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DietaryOptions.HEALTH_GOALS.keys.forEach { goal ->
                        val emoji = when { goal.contains("Diabetic") -> "💧"; goal.contains("Hypertension") -> "❤️"; goal.contains("Fat Loss") -> "🔥"; goal.contains("Muscle") -> "💪"; goal.contains("Fiber") -> "🌾"; goal.contains("Balanced") -> "⚖️"; else -> "" }
                        val isSelected = goal in selectedGoals
                        Surface(shape = RoundedCornerShape(50),
                            color = if (isSelected) NourishColors.PrimaryContainer else NourishColors.SurfaceContainerLowest,
                            border = CardDefaults.outlinedCardBorder().copy(width = if (isSelected) 2.dp else 1.dp,
                                brush = Brush.linearGradient(listOf(if (isSelected) NourishColors.PrimaryContainer else NourishColors.OutlineVariant, if (isSelected) NourishColors.PrimaryContainer else NourishColors.OutlineVariant))),
                            modifier = Modifier.clickable {
                                val balanced = "Balanced / No Restriction"
                                selectedGoals = if (goal == balanced) setOf(balanced) else { val toggled = if (goal in selectedGoals) selectedGoals - goal else selectedGoals + goal; toggled - balanced }
                            }) {
                            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("$emoji $goal", style = MaterialTheme.typography.labelLarge,
                                    color = if (isSelected) NourishColors.OnPrimaryContainer else NourishColors.OnSurfaceVariant)
                                if (isSelected) { Spacer(Modifier.width(6.dp)); Icon(Icons.Filled.Check, "Selected", tint = NourishColors.OnPrimaryContainer, modifier = Modifier.size(14.dp)) }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                val tagPreview = buildDietaryTagString(selectedDietType, selectedGoals)
                if (tagPreview.isNotBlank()) {
                    Surface(shape = RoundedCornerShape(16.dp), color = NourishColors.SurfaceContainerLow,
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NourishColors.OutlineVariant, NourishColors.OutlineVariant)))) {
                        Column(Modifier.fillMaxWidth().padding(16.dp)) {
                            Text("Selected tags", style = MaterialTheme.typography.labelLarge, color = NourishColors.OnSurfaceVariant)
                            Spacer(Modifier.height(4.dp))
                            Text(tagPreview, style = MaterialTheme.typography.bodySmall, color = NourishColors.Primary)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = { registerStep = 1 }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)) { Text("← BACK", style = MaterialTheme.typography.labelLarge) }
                    Button(onClick = {
                        val tags = buildDietaryTagString(selectedDietType, selectedGoals)
                        vm.register(name = name, email = email, password = password, phone = phone.ifBlank { null }, location = location.ifBlank { null }, dietaryTags = tags.ifBlank { null })
                    }, enabled = !state.loading, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NourishColors.PrimaryContainer, contentColor = NourishColors.OnPrimaryContainer),
                        contentPadding = PaddingValues(vertical = 16.dp)) {
                        Text(if (state.loading) "CREATING…" else "REGISTER", style = MaterialTheme.typography.labelLarge)
                    }
                }

                Spacer(Modifier.height(8.dp))
                TextButton(onClick = {
                    vm.register(name = name, email = email, password = password, phone = phone.ifBlank { null }, location = location.ifBlank { null }, dietaryTags = null)
                }, modifier = Modifier.fillMaxWidth()) { Text("Skip for now", color = NourishColors.OnSurfaceVariant) }
            }

            state.error?.let {
                Spacer(Modifier.height(12.dp))
                Surface(shape = RoundedCornerShape(16.dp), color = NourishColors.ErrorContainer) {
                    Text(it, modifier = Modifier.fillMaxWidth().padding(16.dp), color = NourishColors.OnErrorContainer)
                }
            }
        }
    }
}

private fun buildDietaryTagString(dietType: String?, goals: Set<String>): String {
    val tags = mutableSetOf<String>()
    when (dietType) { "VEG" -> tags.add("VEG"); "NON_VEG" -> tags.add("NON_VEG"); "MIXED" -> { tags.add("VEG"); tags.add("NON_VEG") } }
    for (goal in goals) { DietaryOptions.HEALTH_GOALS[goal]?.let { tags.addAll(it) } }
    return tags.joinToString(",")
}
