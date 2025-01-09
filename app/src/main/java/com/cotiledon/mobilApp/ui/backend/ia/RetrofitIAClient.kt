package com.cotiledon.mobilApp.ui.backend.ia

import com.cotiledon.mobilApp.ui.backend.GlobalValues
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitIAClient (private val baseUrl: String){
    val iaApiService : IAApiService

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .retryOnConnectionFailure(true)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        iaApiService = retrofit.create(IAApiService::class.java)
    }

    companion object {
        fun createIAClient() = RetrofitIAClient(GlobalValues.backEndIP)
    }
}