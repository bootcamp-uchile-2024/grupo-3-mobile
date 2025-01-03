package com.cotiledon.mobilApp.ui.backend.cart

import com.cotiledon.mobilApp.ui.backend.GlobalValues
import com.cotiledon.mobilApp.ui.dataClasses.cart.CartProduct
import com.cotiledon.mobilApp.ui.dataClasses.cart.CartProducts
import com.cotiledon.mobilApp.ui.dataClasses.cart.CartResponse
import com.cotiledon.mobilApp.ui.managers.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitCartClient(private val baseUrl: String, private val tokenManager: TokenManager) {
    private val cartApiService: CartApiService

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val token = tokenManager.getToken()
                    ?: throw AuthenticationException("No hay token de autenticacion disponible")

                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()

                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        cartApiService = retrofit.create(CartApiService::class.java)
    }

    suspend fun getUserCart(userId: Int): Response<CartResponse> =
        try {
            cartApiService.getUserCart(userId)
        } catch (e: AuthenticationException) {
            //Enviar de vuelta a fragmento de sign-in
            //TODO
            throw e
        }

    suspend fun createCart(userId: Int) =
        cartApiService.createCart(userId)

    suspend fun addProductToCart(cartId: Int, productId: Int, quantity: Int) =
        cartApiService.addProductToCart(cartId, CartProduct(productId, quantity))

    suspend fun updateProductQuantity(cartId: Int, productId: Int, quantity: Int) =
        cartApiService.updateProductQuantity(cartId, CartProduct(productId, quantity))

    suspend fun removeProductFromCart(cartId: Int, productId: Int) =
        cartApiService.removeProductFromCart(cartId, productId)

    suspend fun replaceCartProducts(cartId: Int, products: List<CartProduct>) =
        cartApiService.replaceCartProducts(cartId, products)

    suspend fun validateCartProducts(cartId: Int, products: List<CartProduct>) =
        try {
            cartApiService.validateCartProducts(
                cartId = cartId,
                request = CartProducts(products)
            )
        } catch (e: AuthenticationException) {
            //Enviar de vuelta a fragmento de sign-in
            //TODO
            throw e
        }

    class AuthenticationException(message: String) : Exception(message)

    companion object {
        fun createCartClient(tokenManager: TokenManager) =
            RetrofitCartClient(GlobalValues.backEndIP, tokenManager)
    }
}