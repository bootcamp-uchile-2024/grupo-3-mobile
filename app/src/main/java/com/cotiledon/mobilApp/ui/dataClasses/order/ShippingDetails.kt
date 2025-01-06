package com.cotiledon.mobilApp.ui.dataClasses.order

import java.io.Serializable

data class ShippingDetails(
    val name: String,
    val lastName: String,
    val address: String,        // Will store street name
    val commune: String,
    val region: String,
    val department: String? = null,
    val streetNumber: String? = null,
    val reference: String? = null,  // Added reference field
    val email: String,
    val phone: String,
    val rut: String? = null
) : Serializable