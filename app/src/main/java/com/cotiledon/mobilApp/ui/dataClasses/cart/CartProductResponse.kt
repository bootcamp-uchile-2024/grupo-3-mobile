package com.cotiledon.mobilApp.ui.dataClasses.cart

import com.cotiledon.mobilApp.ui.dataClasses.plant.Plant

data class CartProductResponse(
    val cantidadProducto: Int,
    val producto: Plant
)