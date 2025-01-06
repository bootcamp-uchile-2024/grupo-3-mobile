package com.cotiledon.mobilApp.ui.backend.user

import com.cotiledon.mobilApp.ui.backend.AuthInterceptor
import com.cotiledon.mobilApp.ui.backend.GlobalValues
import com.cotiledon.mobilApp.ui.dataClasses.profile.UserProfileUpdate
import com.cotiledon.mobilApp.ui.dataClasses.profile.UserRegistrationData
import com.cotiledon.mobilApp.ui.managers.TokenManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor

class RetrofitUserClient (private val baseUrl: String, private val tokenManager: TokenManager) {

    private val userApiService: UserApiService

    init {

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = AuthInterceptor(tokenManager)

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(authInterceptor)
            .build()


        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        userApiService = retrofit.create(UserApiService::class.java)
    }

    suspend fun registerUser(userRegistration: UserRegistrationData?) = userRegistration?.let {
        userApiService.registerUser(
            it
        )
    }

    suspend fun createVisitorProfile() = userApiService.createVisitorProfile()

    suspend fun updateUserProfile(userId: Int, userProfile: UserProfileUpdate) = userApiService.updateUserProfile(userId, userProfile)

    suspend fun getUserProfile() = userApiService.getUserProfile()

    companion object {
        fun createUserClient(tokenManager: TokenManager) =
            RetrofitUserClient(GlobalValues.backEndIP, tokenManager)
    }
}