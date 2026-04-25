package com.teamfab.meallmatch.person.ui.nav

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.teamfab.meallmatch.person.ui.screens.*
import com.teamfab.meallmatch.person.ui.vm.AuthViewModel

@Composable
fun MealMatchNavGraph(navController: NavHostController) {
    val authVm: AuthViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = Routes.Login) {
        composable(Routes.Login) {
            LoginScreen(
                vm = authVm,
                onLoggedIn = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.Home) {
            HomeScreen(
                onMeal = { navController.navigate(Routes.mealDetails(it)) },
                onOrders = { navController.navigate(Routes.Orders) },
                onProfile = { navController.navigate(Routes.Profile) }
            )
        }
        composable(Routes.MealDetails) { entry ->
            MealDetailsScreen(
                mealId = entry.arguments?.getString("mealId").orEmpty(),
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.Orders) {
            OrdersScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.Profile) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    authVm.logout()
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Home) { inclusive = true }
                    }
                }
            )
        }
    }
}