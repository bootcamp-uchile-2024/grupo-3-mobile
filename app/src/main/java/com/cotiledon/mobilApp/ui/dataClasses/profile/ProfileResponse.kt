package com.cotiledon.mobilApp.ui.dataClasses.profile

data class ProfileResponse(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val nombreUsuario: String,
    val email: String,
    val telefono: String?,
    val genero: String?,
    val rut: String,
    val fechaNacimiento: String,
    val rol: String,
    val direcciones: List<String>
)