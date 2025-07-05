package com.example.myapplication.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

enum class Bottom{HOME,ANALYTICS,NFTS}

class BottomNav_ViewModel : ViewModel(){

    private val _BottomNav = MutableLiveData<Bottom>()
    val BottomNav : LiveData<Bottom> = _BottomNav

    fun bottomChoosen(choosen : Bottom)
    {
        _BottomNav.value = choosen
    }


}