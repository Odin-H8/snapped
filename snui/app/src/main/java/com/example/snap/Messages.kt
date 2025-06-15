package com.example.snap

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object Messages {
    var msgs: MutableList<Message> = mutableStateListOf()
    var CurrentUser: String = "Odin"

    suspend fun updateMsgs() {
        withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("http://192.168.68.50:8080/msg")
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    val body = response.body?.string() ?: return@withContext
                    val m: MutableList<Message> = Json.decodeFromString(body)
                    msgs = m
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    suspend fun sendMsg(msg: Message) {
        withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val jsonString = Json.encodeToString(msg)
            val request = Request.Builder()
                .url("http://192.168.68.50:8080/msg")
                .method("POST", jsonString.toRequestBody())
                .build()
            try {
                client.newCall(request).execute()
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Serializable
class Message(
    val sender: String,
    val recipient: String,
    var msg: String,
)

// TODO: everything :( api calls, dynamically update the list when new message is sent, send message, get message