package com.cotiledon.mobilApp.ui.backend.authentication

import com.cotiledon.mobilApp.ui.backend.GlobalValues
import com.cotiledon.mobilApp.ui.dataClasses.authentication.AuthRequest
import com.cotiledon.mobilApp.ui.dataClasses.authentication.AuthResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitAuthClient(private val baseUrl: String) {
    private val authApiService: AuthApiService

    init {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        authApiService = retrofit.create(AuthApiService::class.java)
    }

    suspend fun login (usernameOrEmail: String, password: String): Response<AuthResponse> {
        return authApiService.login(AuthRequest(usernameOrEmail, password))
    }

    companion object{
    fun createAuthClient() = RetrofitAuthClient(GlobalValues.backEndIP)
    }
}