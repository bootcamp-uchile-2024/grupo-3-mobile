package com.cotiledon.mobilApp.ui.dataClasses.profile

data class UserProfileUpdate(
    val nombre: String? = null,
    val apellido: String? = null,
    val nombreUsuario: String? = null,
    val email: String? = null,
    val telefono: String? = null,
    val genero: String? = null,
    val rut: String? = null,
    val fechaNacimiento: String? = null
)