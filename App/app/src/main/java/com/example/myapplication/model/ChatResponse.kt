package com.example.myapplication.model

data class ChatResponse(
    val choices : MutableList<ChatChoice>
)

data class ChatChoice(
    val index : Int,
    val message : ChatMessages
)

data class ChatRequest(
    val model : String = "gpt-3.5-turbo",
    val messages : MutableList<ChatMessages>
)

data class ChatMessages(
    val role : String,
    val content : String
)
