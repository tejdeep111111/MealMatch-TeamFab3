package com.teamfab.meallmatch.person.data

import com.teamfab.meallmatch.person.data.local.SkipStore
import com.teamfab.meallmatch.person.data.local.TokenStore
import com.teamfab.meallmatch.person.data.model.*
import com.teamfab.meallmatch.person.data.remote.MealMatchApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealRepository @Inject constructor(
    private val api: MealMatchApi,
    private val tokenStore: TokenStore,
    private val skipStore: SkipStore
) {
    /* ── Auth ──────────────────────────────────────── */

    suspend fun login(email: String, password: String): AuthResponse {
        val response = api.login(LoginRequest(email, password))
        tokenStore.set(response.token)
        return response
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        phone: String? = null,
        location: String? = null,
        dietaryTags: String? = null
    ): AuthResponse {
        val response = api.register(
            RegisterRequest(name, email, password, phone, location, dietaryTags, role = "USER")
        )
        tokenStore.set(response.token)
        return response
    }

    suspend fun logout() = tokenStore.clear()

    /* ── Meals ─────────────────────────────────────── */

    suspend fun meals(token: String): List<Meal> = api.getMeals("Bearer $token")

    suspend fun compatibleMeals(token: String): List<Meal> = api.getCompatibleMeals("Bearer $token")


    /* ── Subscriptions ─────────────────────────────── */

    suspend fun mySubscriptions(token: String): List<SubscriptionResponse> =
        api.mySubscriptions("Bearer $token")

    suspend fun subscribe(token: String, request: SubscriptionRequest): SubscriptionResponse =
        api.subscribe("Bearer $token", request)

    suspend fun pauseSubscription(token: String, id: String): SubscriptionResponse =
        api.pauseSubscription("Bearer $token", id)

    suspend fun resumeSubscription(token: String, id: String): SubscriptionResponse =
        api.resumeSubscription("Bearer $token", id)

    suspend fun cancelSubscription(token: String, id: String): SubscriptionResponse =
        api.cancelSubscription("Bearer $token", id)

    /* ── Reviews ───────────────────────────────────── */

    suspend fun createReview(token: String, providerId: String, rating: Int, comment: String?): ReviewResponse =
        api.createReview("Bearer $token", ReviewRequest(providerId, rating, comment))

    suspend fun myReviews(token: String): List<ReviewResponse> =
        api.myReviews("Bearer $token")

    /* ── Dietary Preferences ───────────────────────── */

    suspend fun getDietaryTags(token: String): String =
        api.getDietaryTags("Bearer $token")["dietaryTags"].orEmpty()

    suspend fun updateDietaryTags(token: String, tags: String): String {
        val result = api.updateDietaryTags("Bearer $token", mapOf("dietaryTags" to tags))
        return result["dietaryTags"].orEmpty()
    }

    /* ── Providers ─────────────────────────────────── */

    suspend fun providers(token: String): List<ProviderResponse> =
        api.getProviders("Bearer $token")

    suspend fun provider(token: String, id: String): ProviderResponse =
        api.getProvider("Bearer $token", id)

    suspend fun providerReviews(token: String, providerId: String): List<ReviewResponse> =
        api.providerReviews("Bearer $token", providerId)

    /* ── One-Time Delivery Skips ───────────────────── */

    /** A live stream of all currently skipped "subscriptionId::date" keys. */
    val skippedDeliveriesFlow = skipStore.skippedFlow

    /** Mark a single delivery date as skipped (does NOT cancel the subscription). */
    suspend fun skipDelivery(skipKey: String) = skipStore.skip(skipKey)

    /** Restore a previously skipped delivery date. */
    suspend fun unskipDelivery(skipKey: String) = skipStore.unskip(skipKey)
}