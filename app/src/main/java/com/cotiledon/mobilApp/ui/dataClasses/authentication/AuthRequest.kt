package com.cotiledon.mobilApp.ui.dataClasses.authentication

data class AuthRequest(
    val usernameOrEmail: String,
    val password: String
)