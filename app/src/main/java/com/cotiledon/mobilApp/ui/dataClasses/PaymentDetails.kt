package com.cotiledon.mobilApp.ui.dataClasses

import java.io.Serializable

data class PaymentDetails (val paymentMethod: String,
    val cardNumber: String,
    val carHolderName: String,
    val expiryDate: String,
    val securityCode: String) : Serializable