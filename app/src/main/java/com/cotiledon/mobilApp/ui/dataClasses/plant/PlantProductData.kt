package com.cotiledon.mobilApp.ui.dataClasses.plant

data class PlantProductData (
    val id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val image: String,
    val stock: Int,
    val sku: String,
    val categoryId: Int,
    val unitsSold: Int,
    val rating: Int,
    val width: Int,
    val height: Int,
    val length: Int,
    val weight: Int,
    val enabled: Boolean
)