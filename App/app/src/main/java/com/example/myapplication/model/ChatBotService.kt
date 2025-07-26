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
import java.io.IOException

object ChatBotService {
    private val client = OkHttpClient()

    const val gemini_url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"

    private val gson = Gson()

    fun askGemini(
        chatHistory : MutableList<ChatMessages>,
        onResult : (String?) -> Unit
    ){

//        val jsonMessages = chatHistory.joinToString(",") {
//            """{"role":"${it.role}","parts":[{"text":"${it.content}"}]}""".trimIndent()
//        }
//        val json = """{ "contents": [ $jsonMessages] }"""
        val contentList = chatHistory.map {
            mapOf(
                "role" to it.role,
                "parts" to listOf(mapOf("text" to it.content))
            )
        }

        val payload = mapOf("contents" to contentList)
        val json = gson.toJson(payload)  // ✅ safely handles all escaping

        val request = Request.Builder()
            .url("$gemini_url?key=${API_KEY.gemini_api_key}")
            .post(json.toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                onResult("Error : ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val res = gson.fromJson(body,GeminiResponse::class.java)
                val reply = res?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                Log.d("ChatBotService","reply -> $reply,\n res -> $res,\n body -> $body")
                onResult(reply ?: "No Response from Gemini.")
            }

        })

    }
    data class GeminiResponse(val candidates: List<Candidate>)
    data class Candidate(val content: Content)
    data class Content(val parts: List<Part>)
    data class Part(val text: String)

}
