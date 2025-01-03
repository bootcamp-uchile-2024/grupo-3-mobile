package com.cotiledon.mobilApp.ui.dataClasses.plant

data class PlantResponse<T>(
    val data: List<T>,
    val totalItems: Int
)