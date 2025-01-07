package com.cotiledon.mobilApp.ui.dataClasses.plant

import com.cotiledon.mobilApp.ui.dataClasses.catalog.PlantCategory

data class Plant(
    val id: Int,
    val SKU: String,
    val nombre: String,
    val idCategoria: Int,
    val precio: Double,
    val descripcion: String,
    val imagenes: List<ProductImage>,
    val stock: Int,
    val unidadesVendidas: Int,
    val puntuacion: Int,
    val ancho: Int,
    val alto: Int,
    val largo: Int,
    val peso: Int,
    val habilitado: Boolean,
    val categoria: PlantCategory,
    val planta: PlantDetails?
)

