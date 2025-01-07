package com.cotiledon.mobilApp.ui.dataClasses.profile

class VisitorResponse (
    val id: Int,
    val nombre: String,
    val apellido: String,
    val nombreUsuario: String,
    val email: String,
    val rut: String,
    val rol: String,
    val direcciones: List<Direccion>,
    val access_token: String,
    val expToken: Long
)