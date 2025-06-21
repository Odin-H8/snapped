package com.example.snap

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class MessagesViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    init {
        println("messagesviemodel is init'ed")
        loadMessages()
    }

    fun loadMessages() {
        viewModelScope.launch {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("http://192.168.68.50:8080/msg")
                    .build()

                val response = withContext(Dispatchers.IO) {
                    client.newCall(request).execute()
                }

                response.use { response ->
                    val body = response.body?.string() ?: return@launch
                    if (!body.isEmpty()) {
                        val m: MutableList<Message> = Json.decodeFromString(body)
                        _messages.value = m
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("MessagesViewModel","loadMessages failed", e)
            }
        }
    }

    fun sendMessage(msg: Message) {
        viewModelScope.launch {
            try {
                val client = OkHttpClient()
                val jsonString = Json.encodeToString(msg)
                val request = Request.Builder()
                    .url("http://192.168.68.50:8080/msg")
                    .method("POST", jsonString.toRequestBody())
                    .build()

                withContext(Dispatchers.IO) {
                    client.newCall(request).execute()
                }

                loadMessages()
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
}