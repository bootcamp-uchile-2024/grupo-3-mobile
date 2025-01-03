package com.cotiledon.mobilApp.ui.backend.user

import com.cotiledon.mobilApp.ui.dataClasses.profile.UserRegistration
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {
    @POST("usuarios")
    suspend fun registerUser(@Body userRegistration: UserRegistration): Response<Void>
}