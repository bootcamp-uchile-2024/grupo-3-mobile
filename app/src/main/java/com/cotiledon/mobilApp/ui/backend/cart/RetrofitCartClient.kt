package com.cotiledon.mobilApp.ui.backend.cart

import com.cotiledon.mobilApp.ui.backend.GlobalValues
import com.cotiledon.mobilApp.ui.dataClasses.cart.CartProduct
import com.cotiledon.mobilApp.ui.dataClasses.cart.CartProducts
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitCartClient(private val baseUrl: String) {
    private val cartApiService: CartApiService

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

        cartApiService = retrofit.create(CartApiService::class.java)
    }

    suspend fun getUserCart(userId: Int) =
        cartApiService.getUserCart(userId)

    suspend fun addProductToCart(cartId: Int, productId: Int, quantity: Int) =
        cartApiService.addProductToCart(cartId, CartProduct(productId, quantity))

    suspend fun updateProductQuantity(cartId: Int, productId: Int, quantity: Int) =
        cartApiService.updateProductQuantity(cartId, CartProduct(productId, quantity))

    suspend fun removeProductFromCart(cartId: Int, productId: Int) =
        cartApiService.removeProductFromCart(cartId, productId)

    suspend fun replaceCartProducts(cartId: Int, products: List<CartProduct>) =
        cartApiService.replaceCartProducts(cartId, products)

    suspend fun validateCartProducts(cartId: Int, products: List<CartProduct>) =
        cartApiService.validateCartProducts(
            cartId = cartId,
            request = CartProducts(products)
        )

    companion object {
        fun createCartClient() = RetrofitCartClient(GlobalValues.backEndIP)
    }
}