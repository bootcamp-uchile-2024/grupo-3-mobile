package com.cotiledon.mobilApp.ui.backend.order

import com.cotiledon.mobilApp.ui.dataClasses.order.OrderRequest
import com.cotiledon.mobilApp.ui.dataClasses.order.OrderResponse
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