package com.cotiledon.mobilApp.ui.dataClasses

data class OrderResponse(
    val id: Int,
    val idUsuario: Int,
    val fechaCreacion: String,
    val idMedioPago: Int,
    val idEstado: Int,
    val idTipoDespacho: Int,
    val idCarro: Int,
    val fechaEntrega: String,
    val productosPedido: List<ProductoPedido>,
    val direccionEnvio: DireccionEnvio,
    val medioPago: Any,
    val Pago: Any,
    val estadoPedido: Any,
    val tipoDespacho: Any
)