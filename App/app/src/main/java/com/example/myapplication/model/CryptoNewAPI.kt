package com.example.myapplication.model

import com.example.myapplication.R
import retrofit2.http.GET
import retrofit2.http.Query

interface CryptoNewAPI {

    @GET("everything")
    suspend fun getCryptoNews(
        @Query("q") query: String = "cryptocurrency",
        @Query("apikey") apikey : String = "288eca3fff7349adaf24fc6c74aea254"
    ) : NewsResponse

}