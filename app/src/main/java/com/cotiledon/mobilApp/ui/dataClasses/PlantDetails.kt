package com.cotiledon.mobilApp.ui.dataClasses

data class PlantDetails(
    val idProducto: Int,
    val petFriendly: Boolean,
    val toleranciaTemperatura: Int,
    val ciclo: Boolean,
    val altura: String,
    val idEspecie: Int,
    val idColor: Int,
    val idFotoperiodo: Int,
    val idTipoRiego: Int,
    val idHabitoCrecimiento: Int,
    val habitoCrecimiento: String,
    val especie: String,
    val color: String,
    val fotoPeriodo: String,
    val tipoRiego: String
)