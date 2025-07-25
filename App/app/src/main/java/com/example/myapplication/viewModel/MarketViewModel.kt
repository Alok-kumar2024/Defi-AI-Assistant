package com.example.myapplication.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.Article
import com.example.myapplication.model.Coin
import com.example.myapplication.model.RetrofitInstance
import kotlinx.coroutines.launch

class MarketViewModel : ViewModel() {

    private val _newsData = MutableLiveData<MutableList<Article>>()
    val newsData : LiveData<MutableList<Article>> = _newsData

    private val _coinsData = MutableLiveData<MutableList<Coin>>()
    val coinsData : LiveData<MutableList<Coin>> = _coinsData

    private val _coinsDataGainer = MutableLiveData<MutableList<Coin>>()
    val coinsDataGainer : LiveData<MutableList<Coin>> = _coinsDataGainer

    private val _coinsDataLoser = MutableLiveData<MutableList<Coin>>()
    val coinsDataLoser : LiveData<MutableList<Coin>> = _coinsDataLoser

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading : LiveData<Boolean> = _isLoading

    fun fetchCryptoNews(){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.newApi.getCryptoNews()
                _newsData.value = response.articles
            }catch (e : Exception){
                e.printStackTrace()
                Log.e("NewsApi","The Exception is ${e.message}")
            }
            _isLoading.value = false
        }
    }

    fun fetchCryptoCoin(){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.coinsAPI.getCoins()
                _coinsData.value = response

                _coinsDataGainer.value = response.sortedByDescending { it.price_change_percentage_24h }.take(5).toMutableList()
                _coinsDataLoser.value = response.sortedBy { it.price_change_percentage_24h }.take(5).toMutableList()

            }catch (e:Exception){
                e.printStackTrace()
                Log.e("CoinAPI","The Exception is ${e.message}")
            }
            _isLoading.value = false
        }
    }
}