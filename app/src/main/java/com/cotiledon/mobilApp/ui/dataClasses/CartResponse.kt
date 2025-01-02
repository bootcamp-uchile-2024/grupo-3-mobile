package com.cotiledon.mobilApp.ui.dataClasses

data class CartResponse(
    val id: Int,
    val idUsuario: Int,
    val carroProductos: List<CarroProducto>,
    val precioTotal: Double,
    val fecha_cierre: String?,
    val fecha_creacion: String
)