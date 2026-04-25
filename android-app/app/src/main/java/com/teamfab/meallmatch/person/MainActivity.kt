package com.teamfab.meallmatch.person

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.teamfab.meallmatch.person.ui.AppRoot
import com.teamfab.meallmatch.person.ui.theme.NourishTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AppTheme { AppRoot() } }
    }
}

@Composable
private fun AppTheme(content: @Composable () -> Unit) {
    NourishTheme {
        Surface { content() }
    }
}