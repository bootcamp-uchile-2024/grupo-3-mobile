package com.cotiledon.mobilApp.ui.dataClasses

data class Plant(
    val id: Int,
    val SKU: String,
    val nombre: String,
    val idCategoria: Int,
    val precio: Double,
    val descripcion: String,
    val imagen: String?,
    val cantidad: Int,
    val unidadesVendidas: Int,
    val puntuacion: Int,
    val ancho: Int,
    val alto: Int,
    val largo: Int,
    val peso: Int,
    val habilitado: Boolean,
    val categoria: Categoria,
    val planta: PlantDetails?
)

