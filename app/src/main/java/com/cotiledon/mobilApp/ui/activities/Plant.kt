package com.cotiledon.mobilApp.ui.activities

//Se crea la clase Plant que contendrá toda la data extraída del archivo JSON
data class Plant(
    val plantName: String,
    val plantPrice: String,
    val plantDesc: String,
    val plantID: String,
    val plantStock: String,
    val plantCat: String,
    val plantImage: Int
)

