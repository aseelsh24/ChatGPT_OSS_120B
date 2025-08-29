package com.example.helloworld

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.helloworld.network.ApiRequest
import com.example.helloworld.network.ChatMessage
import com.example.helloworld.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Represents the different states of the UI
sealed class ChatUiState {
    object Idle : ChatUiState()
    object Loading : ChatUiState()
    data class Success(val data: String) : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}

class ChatViewModel : ViewModel() {

    private val apiService = RetrofitClient.apiService

    private val _messages = MutableStateFlow<MutableList<Message>>(mutableListOf())
    val messages: StateFlow<List<Message>> = _messages

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
    val uiState: StateFlow<ChatUiState> = _uiState

    init {
        // Add initial assistant message
        _messages.value.add(Message(Message.SENDER_ASSISTANT, "Hello! How can I help you today?"))
    }

    fun sendMessage(userMessageText: String) {
        // Add user message to the list immediately
        val userMessage = Message(Message.SENDER_USER, userMessageText)
        _messages.value.add(userMessage)
        // We need to emit a new list for the StateFlow to update collectors
        _messages.value = _messages.value.toMutableList()

        // Set state to loading
        _uiState.value = ChatUiState.Loading

        viewModelScope.launch {
            try {
                val request = ApiRequest(
                    model = Constants.MODEL_NAME,
                    messages = listOf(ChatMessage(role = "user", content = userMessageText))
                )

                val response = apiService.getChatCompletion(request)

                if (response.isSuccessful) {
                    val assistantResponseContent = response.body()?.choices?.firstOrNull()?.message?.content
                    if (assistantResponseContent != null) {
                        val assistantMessage = Message(Message.SENDER_ASSISTANT, assistantResponseContent)
                        _messages.value.add(assistantMessage)
                        _messages.value = _messages.value.toMutableList()
                        _uiState.value = ChatUiState.Success("Response received")
                    } else {
                        _uiState.value = ChatUiState.Error("Response body is empty or invalid.")
                    }
                } else {
                    _uiState.value = ChatUiState.Error("API Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _uiState.value = ChatUiState.Error("Network Error: ${e.message}")
            }
        }
    }
}
