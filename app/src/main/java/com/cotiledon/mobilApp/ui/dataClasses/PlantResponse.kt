package com.cotiledon.mobilApp.ui.dataClasses

data class PlantResponse<T>(
    val data: List<T>,
    val totalItems: Int
)