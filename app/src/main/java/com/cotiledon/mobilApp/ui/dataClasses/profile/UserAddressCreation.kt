package com.cotiledon.mobilApp.ui.dataClasses.profile

data class UserAddressCreation (
    val nombre: String,
    val apellido: String,
    val region: String,
    val comuna: String,
    val calle: String,
    val numero: String,
    val departamento: String?,
    val referencia: String?
)