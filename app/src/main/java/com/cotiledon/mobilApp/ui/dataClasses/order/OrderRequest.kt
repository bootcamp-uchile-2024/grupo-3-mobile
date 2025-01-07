package com.cotiledon.mobilApp.ui.dataClasses.order

import com.google.gson.annotations.SerializedName

data class OrderRequest(
    val fechaCreacion: String,
    val idMedioPago: Int,
    val idEstado: Int,
    val idTipoDespacho: Int,
    val receptor: String,
    val fechaEntrega: String,
    val direccionEnvio: DireccionEnvio,
    @SerializedName("idxDireccion")
    val idxDireccion: Int
)