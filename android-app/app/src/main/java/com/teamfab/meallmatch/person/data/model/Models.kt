package com.teamfab.meallmatch.person.data.model

data class AuthRequest(val email: String, val password: String)
data class AuthResponse(val token: String)

data class Meal(
    val id: String,
    val title: String,
    val description: String,
    val price: Double
)

data class Order(
    val id: String,
    val mealTitle: String,
    val status: String,
    val total: Double
)