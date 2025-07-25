package com.example.myapplication.model

import retrofit2.http.GET
import retrofit2.http.Query

interface CoinAPI {

    @GET("coins/markets")
    suspend fun getCoins(
        @Query("vs_currency") currency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 200,
        @Query("page") page: Int = 1
    ) : MutableList<Coin>
}

data class Coin(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val current_price: Double,
    val price_change_percentage_24h: Double
)