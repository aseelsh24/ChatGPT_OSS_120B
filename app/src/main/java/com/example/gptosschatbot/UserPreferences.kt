package com.example.gptosschatbot

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class UserPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun savePreferences(
        systemPrompt: String,
        temperature: Float,
        reasoningEffortId: Int,
        enableBrowsing: Boolean
    ) {
        prefs.edit {
            putString(KEY_SYSTEM_PROMPT, systemPrompt)
            putFloat(KEY_TEMPERATURE, temperature)
            putInt(KEY_REASONING_EFFORT_ID, reasoningEffortId)
            putBoolean(KEY_ENABLE_BROWSING, enableBrowsing)
        }
    }

    fun loadSystemPrompt(): String = prefs.getString(KEY_SYSTEM_PROMPT, "") ?: ""
    fun loadTemperature(): Float = prefs.getFloat(KEY_TEMPERATURE, 0.8f)
    fun loadReasoningEffortId(): Int = prefs.getInt(KEY_REASONING_EFFORT_ID, R.id.medium_radio_button)
    fun loadEnableBrowsing(): Boolean = prefs.getBoolean(KEY_ENABLE_BROWSING, false)

    companion object {
        private const val KEY_SYSTEM_PROMPT = "system_prompt"
        private const val KEY_TEMPERATURE = "temperature"
        private const val KEY_REASONING_EFFORT_ID = "reasoning_effort_id"
        private const val KEY_ENABLE_BROWSING = "enable_browsing"
    }
}
