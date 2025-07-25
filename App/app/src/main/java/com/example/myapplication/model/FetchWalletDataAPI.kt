package com.example.myapplication.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface FetchWalletDataAPI {

    @POST("sync-wallet")
    suspend fun syncSpecificWallet( @Body request : SyncWalletRequest) : WalletData

//    @GET("sync-wallets")
//    suspend fun getWalletData() : WalletData
}

data class SyncWalletRequest(
    val userId: String,
    val address: String,
    val chain: String = "eth"  // optional, default = Ethereum
)
