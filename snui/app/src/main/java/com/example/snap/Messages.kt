package com.example.snap

import kotlinx.serialization.Serializable


@Serializable
class Message(
    val sender: String,
    val recipient: String,
    var msg: String,
)

// TODO: everything :( api calls, dynamically update the list when new message is sent, send message, get message