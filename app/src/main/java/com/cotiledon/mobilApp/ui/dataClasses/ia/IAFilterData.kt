package com.cotiledon.mobilApp.ui.dataClasses.ia

import android.os.Parcel
import android.os.Parcelable


data class IAFilterData(
    val idEntorno: Int,
    val petFriendly: Boolean,
    val idToleranciaTemperatura: Int,
    val idIluminacion: Int,
    val idTipoRiego: Int,
    val sizePlant: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idEntorno)
        parcel.writeByte(if (petFriendly) 1 else 0)
        parcel.writeInt(idToleranciaTemperatura)
        parcel.writeInt(idIluminacion)
        parcel.writeInt(idTipoRiego)
        parcel.writeString(sizePlant)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<IAFilterData> {
        override fun createFromParcel(parcel: Parcel): IAFilterData {
            return IAFilterData(parcel)
        }

        override fun newArray(size: Int): Array<IAFilterData?> {
            return arrayOfNulls(size)
        }
    }
}