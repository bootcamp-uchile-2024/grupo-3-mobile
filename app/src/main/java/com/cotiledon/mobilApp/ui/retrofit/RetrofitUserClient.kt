package com.cotiledon.mobilApp.ui.retrofit

import com.cotiledon.mobilApp.ui.dataClasses.UserRegistration
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor

class RetrofitUserClient (private val baseUrl: String) {

    private val userApiService:UserApiService

    init {

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()


        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        userApiService = retrofit.create(UserApiService::class.java)
    }

    suspend fun registerUser(userRegistration: UserRegistration) = userApiService.registerUser(userRegistration)

    companion object {
        fun createUserClient() = RetrofitUserClient(GlobalValues.backEndIP)
    }
}