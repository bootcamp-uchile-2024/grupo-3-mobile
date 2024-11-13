package com.cotiledon.mobilApp.ui.dataClasses

data class PaymentDetails (val paymentMethod: String,
    val cardNumber: String,
    val carHolderName: String,
    val expiryDate: String,
    val securityCode: String)