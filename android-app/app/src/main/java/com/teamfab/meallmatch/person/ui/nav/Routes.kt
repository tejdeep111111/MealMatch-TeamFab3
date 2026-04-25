package com.teamfab.meallmatch.person.ui.nav

object Routes {
    const val Login = "login"
    const val Home = "home"
    const val MealDetails = "meal/{mealId}"
    fun mealDetails(mealId: String) = "meal/$mealId"
    const val Orders = "orders"
    const val Subscriptions = "subscriptions"
    const val Providers = "providers"
    const val ProviderDetails = "provider/{providerId}"
    fun providerDetails(providerId: String) = "provider/$providerId"
    const val DietaryPreferences = "dietary-preferences"
    const val Profile = "profile"
}