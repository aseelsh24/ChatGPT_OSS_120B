package com.example.helloworld

data class Message(
    val sender: String,
    val content: String
) {
    companion object {
        const val SENDER_USER = "user"
        const val SENDER_ASSISTANT = "assistant"
    }
}
