package com.teamfab.meallmatch.person.ui.nav

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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

    NavHost(
        navController = navController,
        startDestination = Routes.Login,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
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
                onSubscriptions = { navController.navigate(Routes.Subscriptions) },
                onProviders = { navController.navigate(Routes.Providers) },
                onProfile = { navController.navigate(Routes.Profile) }
            )
        }
        composable(Routes.MealDetails) { entry ->
            MealDetailsScreen(
                mealId = entry.arguments?.getString("mealId").orEmpty(),
                onBack = { navController.popBackStack() },
                onProvider = { navController.navigate(Routes.providerDetails(it)) },
                onSubscribe = { navController.navigate(Routes.subscribeMeal(it)) }
            )
        }
        composable(Routes.SubscribeMeal) { entry ->
            SubscribeMealScreen(
                mealId = entry.arguments?.getString("mealId").orEmpty(),
                onBack = { navController.popBackStack() },
                onSubscribed = {
                    // Go to subscriptions list after subscribing
                    navController.navigate(Routes.Subscriptions) {
                        popUpTo(Routes.Home)
                    }
                }
            )
        }
        composable(Routes.Subscriptions) {
            SubscriptionsScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.Providers) {
            ProvidersScreen(
                onProvider = { navController.navigate(Routes.providerDetails(it)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.ProviderDetails) { entry ->
            ProviderDetailsScreen(
                providerId = entry.arguments?.getString("providerId").orEmpty(),
                onMeal = { navController.navigate(Routes.mealDetails(it)) },
                onSubscribe = { navController.navigate(Routes.subscribeMeal(it)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.DietaryPreferences) {
            DietaryPreferencesScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.Profile) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onDietaryPreferences = { navController.navigate(Routes.DietaryPreferences) },
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