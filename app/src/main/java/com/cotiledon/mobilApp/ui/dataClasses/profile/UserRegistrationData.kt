package com.cotiledon.mobilApp.ui.dataClasses.profile

import android.os.Parcel
import android.os.Parcelable

data class UserRegistrationData(
    var nombre: String = "",
    var apellido: String = "",
    var nombreUsuario: String = "",
    var email: String = "",
    var telefono: String = "",
    var genero: String = "",
    var rut: String = "",
    var fechaNacimiento: String = "",
    var contrasena: String = "",
    val idRol: Int = 3
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nombre)
        parcel.writeString(apellido)
        parcel.writeString(nombreUsuario)
        parcel.writeString(email)
        parcel.writeString(telefono)
        parcel.writeString(genero)
        parcel.writeString(rut)
        parcel.writeString(fechaNacimiento)
        parcel.writeString(contrasena)
        parcel.writeInt(idRol)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserRegistrationData> {
        override fun createFromParcel(parcel: Parcel): UserRegistrationData {
            return UserRegistrationData(parcel)
        }

        override fun newArray(size: Int): Array<UserRegistrationData?> {
            return arrayOfNulls(size)
        }
    }
}