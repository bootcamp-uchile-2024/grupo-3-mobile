package com.cotiledon.mobilApp.ui.backend.user

import com.cotiledon.mobilApp.ui.dataClasses.profile.ProfileResponse
import com.cotiledon.mobilApp.ui.dataClasses.profile.UserProfileUpdate
import com.cotiledon.mobilApp.ui.dataClasses.profile.UserRegistrationData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApiService {
    @POST("usuarios")
    suspend fun registerUser(@Body userRegistration: UserRegistrationData): Response<Void>

    @GET("usuarios/perfil")
    suspend fun getUserProfile(): Response<ProfileResponse>

    @POST("usuarios/visitante-vacio")
    suspend fun createVisitorProfile(): Response<ProfileResponse>

    @PUT("usuarios/{userId}")
    suspend fun updateUserProfile(
        @Path("userId") userId: Int,
        @Body userProfile: UserProfileUpdate
    ): Response<ProfileResponse>
}