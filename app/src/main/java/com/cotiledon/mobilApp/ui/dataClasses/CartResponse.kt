package com.cotiledon.mobilApp.ui.dataClasses

data class CartResponse(
    val id: Int,
    val idUsuario: Int,
    val precioTotal: Double,
    val carroProductos: List<String>,
    val fechaCreacion: String,
    val fechaCierre: String
)