package com.teamfab.meallmatch.person.ui.nav

object Routes {
    const val Login = "login"
    const val Home = "home"
    const val MealDetails = "meal/{mealId}"
    fun mealDetails(mealId: String) = "meal/$mealId"
    const val Orders = "orders"
    const val Profile = "profile"
}