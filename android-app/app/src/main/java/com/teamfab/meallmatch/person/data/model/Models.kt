package com.teamfab.meallmatch.person.data.model

/* ── Auth ─────────────────────────────────────────── */

data class LoginRequest(val email: String, val password: String)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String? = null,
    val location: String? = null,
    val dietaryTags: String? = null,
    val role: String? = null,
    val cuisineType: String? = null
)

data class AuthResponse(
    val token: String,
    val email: String,
    val role: String
)

/* ── Menu Item (what the backend calls "Meal") ────── */

data class Meal(
    val id: String,
    val providerId: String? = null,
    val providerName: String? = null,
    val name: String = "",
    val mealType: String? = null,
    val dietaryTags: String? = null,
    val price: Double = 0.0,
    val isAvailable: Boolean? = true
) {
    val title: String
        get() = if (name.isBlank()) "Untitled meal" else name
}

/* ── Review ───────────────────────────────────────── */

data class ReviewRequest(
    val providerId: String,
    val rating: Int,
    val comment: String? = null
)

/* ── Subscription ─────────────────────────────────── */

data class SubscriptionRequest(
    val providerId: String,
    val menuItemId: String,
    val daysOfWeek: String? = null,
    val deliveryTime: String? = null,
    val deliveryAddress: String? = null,
    val startDate: String,
    val endDate: String? = null
)

data class SubscriptionResponse(
    val id: String,
    val userId: String? = null,
    val userEmail: String? = null,
    val providerId: String? = null,
    val providerName: String? = null,
    val menuItemId: String? = null,
    val menuItemName: String? = null,
    val daysOfWeek: String? = null,
    val deliveryTime: String? = null,
    val deliveryAddress: String? = null,
    val status: String? = null,
    val startDate: String? = null,
    val endDate: String? = null
)

/* ── Review ───────────────────────────────────────── */

data class ReviewResponse(
    val id: String,
    val userId: String? = null,
    val providerId: String? = null,
    val providerName: String? = null,
    val rating: Int = 0,
    val comment: String? = null,
    val createdAt: String? = null
)

/* ── Provider ─────────────────────────────────────── */

data class ProviderResponse(
    val id: String,
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val location: String? = null,
    val cuisineType: String? = null,
    val rating: Double? = null,
    val isActive: Boolean? = null
)