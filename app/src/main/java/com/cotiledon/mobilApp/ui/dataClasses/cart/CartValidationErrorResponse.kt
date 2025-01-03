package com.cotiledon.mobilApp.ui.dataClasses.cart

data class CartValidationErrorResponse(
    val response: CartProducts,
    val status: Int,
    val message: String,
    val path: String,
    val timestamp: String
)