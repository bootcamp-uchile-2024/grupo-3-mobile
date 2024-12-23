package com.cotiledon.mobilApp.ui.retrofit

import com.cotiledon.mobilApp.ui.dataClasses.OrderRequest
import com.cotiledon.mobilApp.ui.dataClasses.OrderResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderApiService {
    @POST("pedidos/{idUsuario}")
    suspend fun createOrder(
        @Path("idUsuario") userId: Int,
        @Body orderRequest: OrderRequest
    ): Response<OrderResponse>
}