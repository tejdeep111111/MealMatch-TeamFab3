package com.teamfab.meallmatch.person.data.remote

import com.teamfab.meallmatch.person.data.model.*
import retrofit2.http.*

interface MealMatchApi {
    @POST("/auth/login")
    suspend fun login(@Body body: AuthRequest): AuthResponse

    @GET("/meals")
    suspend fun getMeals(@Header("Authorization") bearer: String): List<Meal>

    @GET("/meals/{id}")
    suspend fun getMeal(@Header("Authorization") bearer: String, @Path("id") id: String): Meal

    @GET("/orders/my")
    suspend fun myOrders(@Header("Authorization") bearer: String): List<Order>
}