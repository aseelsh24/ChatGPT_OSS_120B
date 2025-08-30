package com.example.helloworld.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("chat/completions")
    suspend fun getChatCompletion(@Body requestBody: ApiRequest): Response<ApiResponse>
}
