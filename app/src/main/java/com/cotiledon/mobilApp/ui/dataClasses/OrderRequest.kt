package com.cotiledon.mobilApp.ui.dataClasses

data class OrderRequest(
    val fechaCreacion: String,
    val idMedioPago: Int,
    val idEstado: Int,
    val idTipoDespacho: Int,
    val receptor: String,
    val fechaEntrega: String,
    val direccionEnvio: DireccionEnvio
)