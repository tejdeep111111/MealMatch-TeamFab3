package com.teamfab.meallmatch.person.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teamfab.meallmatch.person.ui.theme.NourishColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onDietaryPreferences: () -> Unit = {},
    onLogout: () -> Unit
) {
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
                    Text("Profile", style = MaterialTheme.typography.headlineMedium, color = NourishColors.Primary)
                }
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(20.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Profile avatar
            Box(modifier = Modifier.size(80.dp).clip(CircleShape)
                .background(NourishColors.SurfaceContainer)
                .border(2.dp, NourishColors.OutlineVariant, CircleShape)
                .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center) {
                Text("👤", fontSize = 36.sp)
            }

            Spacer(Modifier.height(8.dp))

            Text("Settings", style = MaterialTheme.typography.titleLarge, color = NourishColors.OnSurface)

            // Diet preferences button
            Surface(modifier = Modifier.fillMaxWidth().clickable { onDietaryPreferences() },
                shape = RoundedCornerShape(16.dp), color = NourishColors.SurfaceContainerLowest,
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NourishColors.OutlineVariant, NourishColors.OutlineVariant)))) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🍽️", fontSize = 24.sp)
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Diet Preferences", style = MaterialTheme.typography.titleMedium, color = NourishColors.OnSurface)
                        Text("Manage your dietary goals", style = MaterialTheme.typography.bodySmall, color = NourishColors.OnSurfaceVariant)
                    }
                    Text("→", fontSize = 20.sp, color = NourishColors.OnSurfaceVariant)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Sign out
            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NourishColors.SurfaceContainerHigh, contentColor = NourishColors.Error),
                contentPadding = PaddingValues(vertical = 16.dp)) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, "Sign Out", modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("SIGN OUT", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}