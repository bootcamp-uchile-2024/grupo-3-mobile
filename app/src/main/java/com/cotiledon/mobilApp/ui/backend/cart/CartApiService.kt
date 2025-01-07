package com.cotiledon.mobilApp.ui.backend.cart


import com.cotiledon.mobilApp.ui.dataClasses.cart.CartProduct
import com.cotiledon.mobilApp.ui.dataClasses.cart.CartProductResponse
import com.cotiledon.mobilApp.ui.dataClasses.cart.CartProducts
import com.cotiledon.mobilApp.ui.dataClasses.cart.CartResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CartApiService {
    @GET("carro-compras/user/{id}")
    suspend fun getUserCart(
        @Path("id") userId: Int
    ): Response<CartResponse>

    @POST("carro-compras/{idUsuario}")
    suspend fun createCart(
        @Path("idUsuario") userId: Int
    ): Response<CartResponse>

    @POST("carro-compras/addProducto/{idCarro}")
    suspend fun addProductToCart(
        @Path("idCarro") cartId: Int,
        @Body cartProduct: CartProduct
    ): Response<CartProductResponse>

    @PATCH("carro-compras/updateProducto/{idCarro}")
    suspend fun updateProductQuantity(
        @Path("idCarro") cartId: Int,
        @Body cartProduct: CartProduct
    ): Response<CartProductResponse>

    @DELETE("carro-compras/removeProducto/{idCarro}/{idProducto}")
    suspend fun removeProductFromCart(
        @Path("idCarro") cartId: Int,
        @Path("idProducto") productId: Int
    ): Response<CartProductResponse>

    @PUT("carro-compras/replaceProductos/{idCarro}")
    suspend fun replaceCartProducts(
        @Path("idCarro") cartId: Int,
        @Body request: List<CartProduct>
    ): Response<List<CartProductResponse>>

    @POST("carro-compras/validateProductosCarro/{idCarro}")
    suspend fun validateCartProducts(
        @Path("idCarro") cartId: Int,
        @Body request: CartProducts
    ): Response<List<CartProductResponse>>
}