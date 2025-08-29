package com.example.helloworld

import android.os.Bundle
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageEditText: TextInputEditText
    private lateinit var sendButton: Button

    private lateinit var messageAdapter: MessageAdapter
    private val messageList = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messagesRecyclerView = findViewById(R.id.messages_recycler_view)
        messageEditText = findViewById(R.id.message_edit_text)
        sendButton = findViewById(R.id.send_button)

        // Setup RecyclerView
        messageAdapter = MessageAdapter(messageList)
        messagesRecyclerView.adapter = messageAdapter
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Add some initial sample messages
        addInitialMessages()

        // Setup send button click listener
        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString()
            if (messageText.isNotBlank()) {
                val userMessage = Message(Message.SENDER_USER, messageText)
                addMessage(userMessage)
                messageEditText.text?.clear()

                // For demonstration, add a canned assistant response
                val assistantResponse = Message(Message.SENDER_ASSISTANT, "Thanks for your message! This is a canned response.")
                addMessage(assistantResponse)
            }
        }
    }

    private fun addInitialMessages() {
        addMessage(Message(Message.SENDER_ASSISTANT, "Hello! This is a sample chat UI. Try sending a message."))
    }

    private fun addMessage(message: Message) {
        messageList.add(message)
        // Notify the adapter that an item was inserted at the last position
        messageAdapter.notifyItemInserted(messageList.size - 1)
        // Scroll to the bottom to see the new message
        messagesRecyclerView.scrollToPosition(messageList.size - 1)
    }
}
