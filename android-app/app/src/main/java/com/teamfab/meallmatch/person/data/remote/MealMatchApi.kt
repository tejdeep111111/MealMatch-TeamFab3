package com.teamfab.meallmatch.person.data.remote

import com.teamfab.meallmatch.person.data.model.*
import retrofit2.http.*

interface MealMatchApi {

    /* ── Auth ──────────────────────────────────────── */

    @POST("/api/auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @POST("/api/auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    /* ── Meals (MenuItems) ─────────────────────────── */

    @GET("/api/meals")
    suspend fun getMeals(@Header("Authorization") bearer: String): List<Meal>

    @GET("/api/meals/compatible")
    suspend fun getCompatibleMeals(@Header("Authorization") bearer: String): List<Meal>


    /* ── Subscriptions ─────────────────────────────── */

    @GET("/api/subscriptions")
    suspend fun mySubscriptions(@Header("Authorization") bearer: String): List<SubscriptionResponse>

    @POST("/api/subscriptions")
    suspend fun subscribe(
        @Header("Authorization") bearer: String,
        @Body body: SubscriptionRequest
    ): SubscriptionResponse

    @PATCH("/api/subscriptions/{id}/pause")
    suspend fun pauseSubscription(
        @Header("Authorization") bearer: String,
        @Path("id") id: String
    ): SubscriptionResponse

    @PATCH("/api/subscriptions/{id}/resume")
    suspend fun resumeSubscription(
        @Header("Authorization") bearer: String,
        @Path("id") id: String
    ): SubscriptionResponse

    @PATCH("/api/subscriptions/{id}/cancel")
    suspend fun cancelSubscription(
        @Header("Authorization") bearer: String,
        @Path("id") id: String
    ): SubscriptionResponse

    /* ── Meal Skips (one-time delivery cancellation) ─── */

    /** Skip one delivery date for a subscription without cancelling the whole plan. */
    @POST("/api/subscriptions/{id}/skip")
    suspend fun skipMeal(
        @Header("Authorization") bearer: String,
        @Path("id") subscriptionId: String,
        @Body body: MealSkipRequest
    ): MealSkipResponse

    /** Remove a previously registered skip (un-skip). Returns 204 No Content. */
    @DELETE("/api/subscriptions/skips/{skipId}")
    suspend fun cancelSkip(
        @Header("Authorization") bearer: String,
        @Path("skipId") skipId: String
    )

    /** List all skips for one of the user's subscriptions. */
    @GET("/api/subscriptions/{id}/skips")
    suspend fun getSkipsForSubscription(
        @Header("Authorization") bearer: String,
        @Path("id") subscriptionId: String
    ): List<MealSkipResponse>

    /* ── Reviews ───────────────────────────────────── */

    @POST("/api/reviews")
    suspend fun createReview(
        @Header("Authorization") bearer: String,
        @Body body: ReviewRequest
    ): ReviewResponse

    @GET("/api/reviews/my")
    suspend fun myReviews(@Header("Authorization") bearer: String): List<ReviewResponse>

    @GET("/api/reviews/provider/{providerId}")
    suspend fun providerReviews(
        @Header("Authorization") bearer: String,
        @Path("providerId") providerId: String
    ): List<ReviewResponse>

    /* ── User / Dietary Preferences ────────────────── */

    @GET("/api/user/dietary-tags")
    suspend fun getDietaryTags(@Header("Authorization") bearer: String): Map<String, String>

    @PUT("/api/user/dietary-tags")
    suspend fun updateDietaryTags(
        @Header("Authorization") bearer: String,
        @Body body: Map<String, String>
    ): Map<String, String>

    /* ── Providers ─────────────────────────────────── */

    @GET("/api/user/providers")
    suspend fun getProviders(@Header("Authorization") bearer: String): List<ProviderResponse>

    @GET("/api/user/providers/{id}")
    suspend fun getProvider(
        @Header("Authorization") bearer: String,
        @Path("id") id: String
    ): ProviderResponse
}