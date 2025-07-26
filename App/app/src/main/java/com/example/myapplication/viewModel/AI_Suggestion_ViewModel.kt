package com.example.myapplication.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.API_KEY
import com.example.myapplication.model.ChatBotService
import com.example.myapplication.model.ChatMessages
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

class AI_Suggestion_ViewModel : ViewModel() {

    val generativeai : GenerativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = "AIzaSyC5Y3ck9HFcM9qBmqA0CBtYarQSn0re6BE"
    )

    val prompt = "You are a crypto expert. Only answer questions about cryptocurrency."

    private val _chatHistory = MutableLiveData<MutableList<ChatMessages>>()
    val chatHistory : LiveData<MutableList<ChatMessages>> = _chatHistory

    private val _waitForResponse = MutableLiveData<Boolean>(false)
    val waitForResponse : LiveData<Boolean> = _waitForResponse

    fun sendMessage(userMsgs : String){

        _waitForResponse.postValue(true)
        Log.d("AI_ViewModel","User Message Send : $userMsgs")

        val userMsg = userMsgs.replace("\"", "\\\"")
        .replace("\n", "\\n")

        val history = _chatHistory.value ?: mutableListOf()

        history.add(ChatMessages("user",userMsg))
        val typing = ChatMessages("model","Typing...")
        history.add(typing)
        _chatHistory.value = history

        ChatBotService.askGemini(history){response->
            response?.let {
                history.add(ChatMessages("model",it))
                history.remove(typing)
                _chatHistory.postValue(history)
                _waitForResponse.postValue(false)
            }
        }
    }
}