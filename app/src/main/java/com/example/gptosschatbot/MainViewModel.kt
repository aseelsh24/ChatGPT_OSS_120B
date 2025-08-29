package com.example.gptosschatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Sealed class to represent the different states of the UI
sealed class ChatUiState {
    object Idle : ChatUiState()
    object Loading : ChatUiState()
    data class Success(val data: String) : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun sendChatRequest(
        message: String,
        systemPrompt: String,
        temperature: Float,
        reasoningEffort: String,
        enableBrowsing: Boolean
    ) {
        viewModelScope.launch {
            _uiState.value = ChatUiState.Loading
            val result = GradioRepository.getChatResponse(
                message,
                systemPrompt,
                temperature,
                reasoningEffort,
                enableBrowsing
            )
            _uiState.value = result.fold(
                onSuccess = { response -> ChatUiState.Success(response) },
                onFailure = { error -> ChatUiState.Error(error.message ?: "An unknown error occurred") }
            )
        }
    }
}
