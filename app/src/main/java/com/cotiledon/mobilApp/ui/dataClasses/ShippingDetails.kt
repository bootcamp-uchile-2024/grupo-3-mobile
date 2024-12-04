package com.cotiledon.mobilApp.ui.dataClasses

import java.io.Serializable

data class ShippingDetails (val name: String,
    val address: String,
    val city: String,
    val region: String,
    val zipCode: String,
    val phone: String,
    val email: String) : Serializable