package com.example.gptosschatbot

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.gptosschatbot.databinding.ActivityMainBinding
import io.noties.markwon.Markwon
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var markwon: Markwon
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        markwon = Markwon.create(this)
        userPreferences = UserPreferences(this)

        loadSettings()
        setupSendButton()
        observeViewModel()
    }

    private fun loadSettings() {
        binding.systemPromptEditText.setText(userPreferences.loadSystemPrompt())
        binding.temperatureSlider.value = userPreferences.loadTemperature()
        binding.reasoningRadioGroup.check(userPreferences.loadReasoningEffortId())
        binding.browsingSwitch.isChecked = userPreferences.loadEnableBrowsing()
    }

    private fun saveSettings() {
        userPreferences.savePreferences(
            systemPrompt = binding.systemPromptEditText.text.toString(),
            temperature = binding.temperatureSlider.value,
            reasoningEffortId = binding.reasoningRadioGroup.checkedRadioButtonId,
            enableBrowsing = binding.browsingSwitch.isChecked
        )
    }

    private fun setupSendButton() {
        binding.sendButton.setOnClickListener {
            val message = binding.messageEditText.text.toString()
            if (message.isBlank()) {
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save settings on send
            saveSettings()

            val systemPrompt = binding.systemPromptEditText.text.toString()
            val temperature = binding.temperatureSlider.value
            val selectedReasoningId = binding.reasoningRadioGroup.checkedRadioButtonId
            val reasoningEffort = findViewById<RadioButton>(selectedReasoningId).text.toString().lowercase()
            val enableBrowsing = binding.browsingSwitch.isChecked

            viewModel.sendChatRequest(
                message,
                systemPrompt,
                temperature,
                reasoningEffort,
                enableBrowsing
            )
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is ChatUiState.Idle -> {
                            binding.progressBar.visibility = View.GONE
                            binding.sendButton.isEnabled = true
                        }
                        is ChatUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.resultTextView.text = ""
                            binding.sendButton.isEnabled = false
                        }
                        is ChatUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.sendButton.isEnabled = true
                            // Use Markwon to render the markdown
                            markwon.setMarkdown(binding.resultTextView, state.data)
                        }
                        is ChatUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.sendButton.isEnabled = true
                            binding.resultTextView.text = "Error: ${state.message}"
                            Toast.makeText(this@MainActivity, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}
