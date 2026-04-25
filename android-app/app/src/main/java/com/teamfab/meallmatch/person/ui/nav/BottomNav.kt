package com.teamfab.meallmatch.person.ui.nav

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.teamfab.meallmatch.person.ui.theme.NourishColors

data class BottomNavItem(
    val route: String,
    val label: String,
    val emoji: String
)

val bottomNavItems = listOf(
    BottomNavItem(Routes.Home, "Meals", "🍽️"),
    BottomNavItem(Routes.Dashboard, "Upcoming", "📅"),
    BottomNavItem(Routes.Subscriptions, "Subscriptions", "📋"),
    BottomNavItem(Routes.Providers, "Providers", "👩‍🍳"),
    BottomNavItem(Routes.Profile, "Profile", "👤")
)

/** Routes that show the persistent bottom nav bar */
val tabRoutes = bottomNavItems.map { it.route }.toSet()

@Composable
fun MealMatchBottomBar(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Only show if we're on a tab route
    if (currentRoute !in tabRoutes) return

    NavigationBar(
        containerColor = NourishColors.Surface,
        contentColor = NourishColors.OnSurface,
        tonalElevation = 2.dp
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Always pop back to the true graph start destination so the
                            // back stack never grows endlessly and the Meals tab is always reachable
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Text(
                        item.emoji,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                label = {
                    Text(
                        item.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selected) NourishColors.Primary else NourishColors.OnSurfaceVariant
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NourishColors.Primary,
                    unselectedIconColor = NourishColors.OnSurfaceVariant,
                    indicatorColor = NourishColors.PrimaryContainer.copy(alpha = 0.3f)
                )
            )
        }
    }
}


