package com.example.myapplication.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.ChatBotService
import com.example.myapplication.model.ChatMessages

class AI_Suggestion_ViewModel : ViewModel() {

    private val _chatHistory = MutableLiveData<MutableList<ChatMessages>>()
    val chatHistory : LiveData<MutableList<ChatMessages>> = _chatHistory

    private val _waitForResponse = MutableLiveData<Boolean>(false)
    val waitForResponse : LiveData<Boolean> = _waitForResponse

    fun sendMessage(userMsg : String){

        _waitForResponse.postValue(true)
        Log.d("AI_ViewModel","User Message Send : $userMsg")

        val updatedHistory = _chatHistory.value ?: mutableListOf()

        updatedHistory.add(ChatMessages("user",userMsg))
        val typing = ChatMessages("assistant","Typing....")
        updatedHistory.add(typing)
        _chatHistory.postValue(updatedHistory)

        val messageForAPI = mutableListOf<ChatMessages>()
        messageForAPI.add(ChatMessages("system", "You are a crypto assistant. Only answer crypto-related questions."))
        messageForAPI.addAll(updatedHistory)

        ChatBotService.askAiBot(messageForAPI){reply->
            updatedHistory.add(ChatMessages("assistant",reply?: "Something Went Wrong"))
            updatedHistory.remove(typing)
            _chatHistory.postValue(updatedHistory)
            _waitForResponse.postValue(false)
        }
    }
}