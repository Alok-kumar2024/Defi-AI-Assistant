package com.example.myapplication.model

class Repository {

   suspend fun SyncUsersWallet(api: FetchWalletDataAPI, request : SyncWalletRequest) : WalletData {
        return api.syncSpecificWallet(request)
    }
}