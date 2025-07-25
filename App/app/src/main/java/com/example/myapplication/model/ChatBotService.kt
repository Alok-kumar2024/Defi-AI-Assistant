package com.example.myapplication.model

import android.util.Log
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.http.Query
import java.io.IOException

object ChatBotService {
    private val client = OkHttpClient()

//    const val OPENAI_API_KEY = "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"
    const val OPENAI_API_KEY ="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"
    const val OPENAI_URL = "https://api.openai.com/v1/chat/completions"

    fun askAiBot(message : MutableList<ChatMessages>,callBack : (String?) -> Unit){

        val requestBody = ChatRequest(messages = message)
        val gson = Gson()
        val json = gson.toJson(requestBody)

        val body = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(OPENAI_URL)
            .addHeader("Authorization", "Bearer ${OPENAI_API_KEY}")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callBack(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let {
                    Log.d("AI_RAW_RESPONSE", it) //

                    if (response.code == 429 || response.code == 400) {
                        Log.e("AI", "Quota exceeded or invalid request")
                        callBack("Oops! You’ve hit the usage limit. Try again later.")
                        return
                    }

                    val result = gson.fromJson(it,ChatResponse::class.java)

                    val reply = result?.choices?.firstOrNull()?.message?.content

                    Log.d("AI", "The Message: $reply")
                    callBack(reply)
                } ?: callBack(null)
            }

        })
    }
}