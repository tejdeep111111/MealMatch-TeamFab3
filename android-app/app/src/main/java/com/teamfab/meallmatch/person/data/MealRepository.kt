package com.teamfab.meallmatch.person.data

import com.teamfab.meallmatch.person.data.local.TokenStore
import com.teamfab.meallmatch.person.data.model.AuthRequest
import com.teamfab.meallmatch.person.data.model.Meal
import com.teamfab.meallmatch.person.data.model.Order
import com.teamfab.meallmatch.person.data.remote.MealMatchApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealRepository @Inject constructor(
    private val api: MealMatchApi,
    private val tokenStore: TokenStore
) {
    suspend fun login(email: String, password: String): String {
        val token = api.login(AuthRequest(email, password)).token
        tokenStore.set(token)
        return token
    }

    suspend fun logout() = tokenStore.clear()

    suspend fun meals(token: String): List<Meal> = api.getMeals("Bearer $token")
    suspend fun meal(token: String, id: String): Meal = api.getMeal("Bearer $token", id)
    suspend fun myOrders(token: String): List<Order> = api.myOrders("Bearer $token")
}