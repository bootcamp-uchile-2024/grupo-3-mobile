package com.cotiledon.mobilApp.ui.backend.order

import com.cotiledon.mobilApp.ui.backend.GlobalValues
import com.cotiledon.mobilApp.ui.dataClasses.order.OrderRequest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitOrderClient(private val baseUrl: String) {
    private val orderApiService: OrderApiService

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        orderApiService = retrofit.create(OrderApiService::class.java)
    }

    suspend fun createOrder(userId: Int, orderRequest: OrderRequest) =
        orderApiService.createOrder(userId, orderRequest)

    companion object {
        fun createOrderClient() = RetrofitOrderClient(GlobalValues.backEndIP)
    }
}