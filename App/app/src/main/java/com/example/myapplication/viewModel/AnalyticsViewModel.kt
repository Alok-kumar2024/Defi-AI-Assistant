package com.example.myapplication.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.Analytics
import com.example.myapplication.model.TokenPie
import com.example.myapplication.model.WalletData
import com.google.firebase.firestore.FirebaseFirestore

class AnalyticsViewModel : ViewModel() {

    private val _analytics = MutableLiveData<Analytics?>()
    private val _tokenListData = MutableLiveData<MutableList<TokenPie>>()
    private val _isLoading = MutableLiveData<Boolean>(true)

    val analytics : LiveData<Analytics?> = _analytics
    val tokenListData : LiveData<MutableList<TokenPie>> = _tokenListData
    val isLoading : LiveData<Boolean> = _isLoading

    fun AnalyticsData(userid : String, address: String){

        _isLoading.value = true

        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("USERS").document(userid).collection("wallets").document(address)
            .addSnapshotListener { firestoreQuery, error ->
                if (error != null)
                {
                    Log.e("FireStore","Error : Listner Failed : ${error.message}")
                    _isLoading.value = false
                    return@addSnapshotListener
                }
                if (firestoreQuery != null)
                {
                    try {
                        val data = firestoreQuery.toObject(WalletData::class.java)
                        if (data != null){

                            val walletData = data

                            _analytics.value = data.analytics

                            _tokenListData.value = _analytics.value?.tokenDistribution?.toMutableList()

                            Log.d("FireStore","The Analytics Data : ${_analytics.value}")

                        }else{
                            Log.w("FireStore", "Snapshot exists but toObject() returned null.")
                        }

                    }catch (e:Exception){
                        Log.e("FireStore", "Deserialization failed", e)
                    }finally {
                        _isLoading.value = false
                    }
                }
            }
    }
}