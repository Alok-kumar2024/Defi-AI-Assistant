package com.example.myapplication.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val NEWS_BASE_URL = "https://newsapi.org/v2/"
    private const val COIN_BASE_URL = "https://api.coingecko.com/api/v3/"

    val newApi = Retrofit.Builder()
        .baseUrl(NEWS_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CryptoNewAPI::class.java)

    val coinsAPI = Retrofit.Builder()
        .baseUrl(COIN_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CoinAPI::class.java)

}