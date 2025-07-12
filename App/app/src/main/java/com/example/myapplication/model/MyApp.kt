package com.example.myapplication.model

import android.app.Application
import android.util.Log
import com.walletconnect.android.Core
import com.walletconnect.android.CoreClient
import com.walletconnect.android.relay.ConnectionType
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()



        val metaData = Core.Model.AppMetaData(
            name = "Defi AI Assistant",
            description = "AI crypto App",
            url = "https://yourapp.com",
            icons = listOf("https://yourapp.com/icon.png"),
            redirect = "defiai://callback"
        )

        CoreClient.initialize(
            metaData = metaData,
            relayServerUrl = "wss://relay.walletconnect.com?projectId=141281e6ec8c25ea34e7dd4497ab5d58",
            connectionType = ConnectionType.AUTOMATIC,
            application = this,
            onError = { error ->
                Log.e("WalletConnect", "CoreClient error: ${error.throwable}")
            }
        )

//        Web3Wallet.initialize(
//            Wallet.Params.Init(core = CoreClient),
//            onSuccess = {
//                Log.d("WalletConnect","Web3Wallet initialized")
//
//            },
//            onError = {
//                Log.e("Walletconnect","Web3Wallet Error${it.throwable}")
//            }
//        )

        SignClient.initialize(
            init = Sign.Params.Init(CoreClient),
            onSuccess = {
                Log.d("WalletConnect","Web3Wallet initialized")
            },
            onError = {
                Log.e("Walletconnect","Web3Wallet Error${it.throwable}")
            })



    }

}