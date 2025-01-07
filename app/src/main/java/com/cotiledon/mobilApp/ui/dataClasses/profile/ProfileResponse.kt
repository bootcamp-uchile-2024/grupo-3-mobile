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
    val direcciones: List<String>,
    val access_token: String?,
    val expToken: Long?
)

fun ProfileResponse.toVisitorResponse(): VisitorResponse {
    // We create a new VisitorResponse with the mapped fields
    return VisitorResponse(
        // Basic identification fields are directly mapped
        id = this.id,
        nombre = this.nombre,
        apellido = this.apellido,
        nombreUsuario = this.nombreUsuario,
        email = this.email,
        rut = this.rut,

        // For visitor profiles, we know the role will always be "Visitante"
        rol = "Visitante",

        // Convert the string list of addresses to proper Direccion objects
        // If the addresses list is empty or invalid, we provide an empty list
        direcciones = this.direcciones.mapNotNull { direccionStr ->
            try {
                val parts = direccionStr.split(",")
                Direccion(
                    id = parts[0].toInt(),
                    comuna = parts[1],
                    calle = parts[2],
                    numero = parts[3],
                    departamento = parts.getOrNull(4),
                    referencia = parts.getOrNull(5)
                )
            } catch (e: Exception) {
                null
            }
        },

        // These fields would typically come from the ProfileResponse
        // You'll need to add them to your ProfileResponse class if they're not there
        access_token = this.access_token ?: "",  // Add this field to ProfileResponse
        expToken = this.expToken ?: 0L          // Add this field to ProfileResponse
    )
}