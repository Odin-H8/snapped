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
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class MessagesViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val client = OkHttpClient()
    private lateinit var webSocket: WebSocket

    init {
        loadMessages()
        connectWebsocket()
    }

    private fun connectWebsocket() {
        val request = Request.Builder()
            .url("ws://192.168.68.50:8080/ws")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                println("Websocket opened")
            }
            override fun onMessage(webSocket: WebSocket, text: String) {
                println("Websocket message recived: $text")
                val message = Json.decodeFromString<Message>(text)
                _messages.value = _messages.value + message
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                t.printStackTrace()
            }
        })
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
        val json = Json.encodeToString(msg)
        webSocket.send(json)
    }

    override fun onCleared() {
        super.onCleared()
        webSocket.close(1000, "viewModel cleared...???")
    }
}