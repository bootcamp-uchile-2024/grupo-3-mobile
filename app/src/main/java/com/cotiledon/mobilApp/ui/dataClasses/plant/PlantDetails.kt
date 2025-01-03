package com.cotiledon.mobilApp.ui.dataClasses.plant

data class PlantDetails(
    val idProducto: Int,
    val petFriendly: Boolean,
    val ciclo: Boolean,
    val especie: String,
    val idColor: Int,
    val idFotoperiodo: Int,
    val idTipoRiego: Int,
    val idHabitoCrecimiento: Int,
    val habitoCrecimiento: String,
    val color: String,
    val fotoPeriodo: String,
    val tipoRiego: String,
    val idToleranciaTemperatura: Int,
    val idEntorno: Int,
    val idIluminacion: Int,
    val idTamano: Int,
    val entorno: String,
    val iluminacion: String,
    val toleranciaTemperatura: String,
    val tamano: String
)