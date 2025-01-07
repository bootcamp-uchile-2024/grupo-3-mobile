package com.cotiledon.mobilApp.ui.dataClasses.profile

data class Direccion(
    val id: Int,
    val comuna: String,
    val calle: String,
    val numero: String,
    val departamento: String?,
    val referencia: String?
)