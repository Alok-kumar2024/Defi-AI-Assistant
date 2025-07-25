package com.example.myapplication.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.FetchWalletDataAPI
import com.example.myapplication.model.NetWorth
import com.example.myapplication.model.Repository
import com.example.myapplication.model.SyncWalletRequest
import com.example.myapplication.model.TokenBalance
import com.example.myapplication.model.Transaction
import com.example.myapplication.model.WalletData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okio.Timeout
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class WalletDataViewModel : ViewModel() {

    private val repository = Repository()

    private val _SyncWallet = MutableLiveData<WalletData>()
    private val _isLoading = MutableLiveData<Boolean>(true)
    private val _walletData = MutableLiveData<WalletData?>()
    private val _loadingAnimation = MutableLiveData<Boolean>(true)
    private val _portfolioValue = MutableLiveData<NetWorth?>()
    private val _transactionData = MutableLiveData<MutableList<Transaction>>()

    val SyncWallet : LiveData<WalletData> = _SyncWallet
    val isLoading : LiveData<Boolean> = _isLoading
    val walletData : LiveData<WalletData?> = _walletData
    val loadingAnimation : LiveData<Boolean> = _loadingAnimation
    val portfolioValue : LiveData<NetWorth?> = _portfolioValue
    val transactionData : LiveData<MutableList<Transaction>> = _transactionData

    //Crypto Token Adapter

    private val _tokenData = MutableLiveData<MutableList<TokenBalance>>()
    val tokenData : LiveData<MutableList<TokenBalance>> = _tokenData

    private val httpsClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(20,TimeUnit.SECONDS)
            .writeTimeout(20,TimeUnit.SECONDS)
            .readTimeout(60,TimeUnit.SECONDS)
            .callTimeout(90,TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }



    val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://wallet-watcher-server.onrender.com/api/")
            .client(httpsClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FetchWalletDataAPI::class.java)
    }


    fun SyncWallet(userid : String, address : String){

        Log.d("WalletSync", "Calling syncSpecificWallet with userId=$userid, address=$address")

        viewModelScope.launch {
             try {
                 val request = SyncWalletRequest(userid,address)
                 val response = repository.SyncUsersWallet(retrofit,request)

                 _SyncWallet.postValue(response)

                 Log.d("WalletSync", "Calling syncSpecificWallet with userId=$userid, address=$address")

             }catch (e : Exception){

                 Log.e("WalletSync","Error Calling API : ${e.message}")

             }
            _isLoading.value =false
         }
    }

    fun walletDataFromFireStore(userid : String, address: String){
        _loadingAnimation.value = true

        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("USERS").document(userid).collection("wallets").document(address)
            .addSnapshotListener { firestoreQuery, error ->
                if (error != null)
                {
                    Log.e("FireStore","Error : Listner Failed : ${error.message}")
                    _loadingAnimation.value = false
                    return@addSnapshotListener
                }
                if (firestoreQuery != null)
                {
                    try {
                        val data = firestoreQuery.toObject(WalletData::class.java)
                        if (data != null){

                            _walletData.value = data
                            _tokenData.value = data.tokenBalances.toMutableList()
                            _portfolioValue.value = data.netWorth

                            val txList = data.recentTransactions.sortedByDescending { it.timestamp }.toMutableList()

                            _transactionData.value = txList

                            Log.d("FireStore","The Wallet Data : ${_walletData.value}")

                        }else{
                            Log.w("FireStore", "Snapshot exists but toObject() returned null.")
                        }

                    }catch (e:Exception){
                        Log.e("FireStore", "Deserialization failed", e)
                    }finally {
                        _loadingAnimation.value = false
                    }
                }
            }
    }


}