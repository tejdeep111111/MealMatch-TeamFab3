package com.teamfab.meallmatch.person.ui.nav

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.teamfab.meallmatch.person.ui.screens.*
import com.teamfab.meallmatch.person.ui.theme.NourishColors
import com.teamfab.meallmatch.person.ui.vm.AuthViewModel

@Composable
fun MealMatchNavGraph(navController: NavHostController) {
    val authVm: AuthViewModel = hiltViewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = NourishColors.Background,
        bottomBar = {
            if (currentRoute in tabRoutes) {
                MealMatchBottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Login,
            modifier = Modifier.padding(innerPadding),
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
            composable(Routes.Dashboard) {
                UpcomingDeliveriesScreen()
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
}