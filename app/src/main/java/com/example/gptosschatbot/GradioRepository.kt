package com.example.gptosschatbot

import com.gradio.client.GradioClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GradioRepository {

    private const val API_HOST = "amd/gpt-oss-120b-chatbot"
    private const val API_NAME = "/chat"

    suspend fun getChatResponse(
        message: String,
        systemPrompt: String,
        temperature: Float,
        reasoningEffort: String,
        enableBrowsing: Boolean
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val client = GradioClient.builder()
                    .space(API_HOST)
                    .build()

                val response = client.predict(
                    fnName = API_NAME,
                    data = arrayOf(
                        message,
                        systemPrompt,
                        temperature,
                        reasoningEffort,
                        enableBrowsing
                    )
                )

                // Assuming the response is a string in the first element
                val result = response.getOrNull(0) as? String
                if (result != null) {
                    Result.success(result)
                } else {
                    Result.failure(Exception("Failed to parse API response or response is empty."))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }
}
