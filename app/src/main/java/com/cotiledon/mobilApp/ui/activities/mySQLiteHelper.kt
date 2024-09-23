package com.cotiledon.mobilApp.ui.activities

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class mySQLiteHelper(context: Context) : SQLiteOpenHelper(
    context, "preferencias.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        val orderCreation = "CREATE TABLE favoritos(_id int PRIMARY KEY, nombre varchar(30), precio double)"
        db?.execSQL(orderCreation)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val orderDelete = "DROP TABLE IF EXISTS favoritos"
        db?.execSQL(orderDelete)
        onCreate(db)
    }

    fun addData(id: Int, name: String, price: Double) {
        val datos = ContentValues()
        datos.put("_id", id)
        datos.put("plant name", name)
        datos.put("plant price", price)

        val db = this.writableDatabase
        db.insert("favoritos", null, datos)
        db.close()
    }

}