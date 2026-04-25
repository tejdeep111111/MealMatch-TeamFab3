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

data class ReviewRequest(
    val providerId: String,
    val rating: Int,
    val comment: String? = null
)

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

/* ── Meal Skip (server-side one-time delivery skip) ── */

/** Sent to POST /api/subscriptions/{id}/skip */
data class MealSkipRequest(
    val skipDate: String,         // "yyyy-MM-dd"
    val reason: String? = null
)

/** Returned by the backend for every skip record */
data class MealSkipResponse(
    val id: String,               // UUID — needed to DELETE (un-skip)
    val subscriptionId: String,
    val userEmail: String? = null,
    val menuItemName: String? = null,
    val deliveryTime: String? = null,
    val daysOfWeek: String? = null,
    val skipDate: String,         // "yyyy-MM-dd"
    val reason: String? = null,
    val createdAt: String? = null
)

/* ── Upcoming Delivery (computed client-side) ─────── */

/**
 * Represents a single upcoming delivery slot computed from an active subscription.
 * The user can skip a specific date without affecting the overall subscription schedule.
 * When [isSkipped] is true, [skipId] holds the server-side MealSkip.id needed to un-skip.
 */
data class UpcomingDelivery(
    val subscriptionId: String,
    val mealName: String,
    val providerName: String?,
    val dateStr: String,          // "yyyy-MM-dd"
    val displayDate: String,      // e.g. "Mon, Apr 28"
    val daysUntil: Int,           // 0 = today, 1 = tomorrow, …
    val deliveryTime: String?,
    val deliveryAddress: String?,
    val isSkipped: Boolean = false,
    val skipId: String? = null    // non-null when isSkipped — the MealSkip UUID from the server
)
