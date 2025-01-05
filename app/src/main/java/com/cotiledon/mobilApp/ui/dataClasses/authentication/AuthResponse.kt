package com.cotiledon.mobilApp.ui.dataClasses.authentication

data class AuthResponse(
    val access_token: String,
    val id: Int,
    val expToken: Long
)