package com.example.helloworld

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: ChatViewModel by viewModels()

    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageEditText: TextInputEditText
    private lateinit var sendButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Views
        messagesRecyclerView = findViewById(R.id.messages_recycler_view)
        messageEditText = findViewById(R.id.message_edit_text)
        sendButton = findViewById(R.id.send_button)
        progressBar = findViewById(R.id.progress_bar)

        // Setup RecyclerView
        setupRecyclerView()

        // Setup Observers
        observeMessages()
        observeUiState()

        // Setup Send Button
        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString()
            if (messageText.isNotBlank()) {
                viewModel.sendMessage(messageText)
                messageEditText.text?.clear()
            }
        }
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter(mutableListOf())
        messagesRecyclerView.adapter = messageAdapter
        val layoutManager = LinearLayoutManager(this)
        messagesRecyclerView.layoutManager = layoutManager
    }

    private fun observeMessages() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.messages.collect { messages ->
                    messageAdapter.submitList(messages)
                    // Scroll to the bottom to see the new message
                    if (messages.isNotEmpty()) {
                        messagesRecyclerView.scrollToPosition(messages.size - 1)
                    }
                }
            }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is ChatUiState.Idle, is ChatUiState.Success -> {
                            progressBar.visibility = View.GONE
                            sendButton.isEnabled = true
                        }
                        is ChatUiState.Loading -> {
                            progressBar.visibility = View.VISIBLE
                            sendButton.isEnabled = false
                        }
                        is ChatUiState.Error -> {
                            progressBar.visibility = View.GONE
                            sendButton.isEnabled = true
                            Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}
