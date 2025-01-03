package com.cotiledon.mobilApp.ui.backend.authentication

import com.cotiledon.mobilApp.ui.dataClasses.authentication.AuthRequest
import com.cotiledon.mobilApp.ui.dataClasses.authentication.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body authRequest: AuthRequest): Response<AuthResponse>
}