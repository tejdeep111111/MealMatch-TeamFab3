package com.teamfab.meallmatch.person.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.teamfab.meallmatch.person.ui.nav.MealMatchNavGraph

@Composable
fun AppRoot() {
    MealMatchNavGraph(navController = rememberNavController())
}