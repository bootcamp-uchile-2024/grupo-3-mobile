package com.cotiledon.mobilApp.ui.dataClasses

data class UserRegistration (
    val contrasena: String,
    val nombre: String,
    val apellido: String,
    val nombreUsuario: String,
    val email: String,
    val telefono: String? = null,
    val genero: String? = null,
    val rut: String,
    val fechaNacimiento: String,
    val idRol: Int = 3)