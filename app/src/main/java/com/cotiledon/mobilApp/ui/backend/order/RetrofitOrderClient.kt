package com.cotiledon.mobilApp.ui.backend.order

import com.cotiledon.mobilApp.ui.backend.AuthInterceptor
import com.cotiledon.mobilApp.ui.backend.GlobalValues
import com.cotiledon.mobilApp.ui.dataClasses.order.OrderRequest
import com.cotiledon.mobilApp.ui.helpers.OrderRequestSerializer
import com.cotiledon.mobilApp.ui.managers.TokenManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitOrderClient(private val baseUrl: String, private val tokenManager: TokenManager) {
    private val orderApiService: OrderApiService

    private fun createGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(OrderRequest::class.java, OrderRequestSerializer())
            .create()
    }

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = AuthInterceptor(tokenManager)

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(authInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(createGson()))
            .build()

        orderApiService = retrofit.create(OrderApiService::class.java)
    }

    suspend fun createOrder(userId: Int, orderRequest: OrderRequest) =
        orderApiService.createOrder(userId, orderRequest)

    companion object {
        fun createOrderClient(tokenManager: TokenManager) =
            RetrofitOrderClient(GlobalValues.backEndIP, tokenManager)
    }
}