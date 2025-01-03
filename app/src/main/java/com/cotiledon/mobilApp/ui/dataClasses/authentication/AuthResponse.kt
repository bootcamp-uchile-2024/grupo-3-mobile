package com.cotiledon.mobilApp.ui.dataClasses.authentication

data class AuthResponse(
    val accessToken: String,
    val id: Int,
    val expToken: Long
)