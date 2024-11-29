package com.cotiledon.mobilApp.ui.dataClasses

data class UserRegistration (
    val contrasena: String,
    val nombre: String,
    val apellido: String,
    val nombreUsuario: String,
    val email: String,
    val telefono: String,
    val genero: String,
    val rut: String,
    val fechaNacimiento: String,
    val tipoUsuarioId: Int = 3)