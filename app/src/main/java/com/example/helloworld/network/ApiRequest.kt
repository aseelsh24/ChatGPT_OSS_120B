package com.example.helloworld.network

data class ApiRequest(
    val model: String,
    val messages: List<ChatMessage>
)

data class ChatMessage(
    val role: String,
    val content: String
)
