package com.cotiledon.mobilApp.ui.backend

import com.cotiledon.mobilApp.ui.managers.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // Get the original request
        val originalRequest = chain.request()

        // Get the token from TokenManager
        // If there's no token, proceed with the original request
        val token = tokenManager.getToken() ?: return chain.proceed(originalRequest)

        // Create a new request with the Authorization header
        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        // Proceed with the new request
        return chain.proceed(newRequest)
    }
}