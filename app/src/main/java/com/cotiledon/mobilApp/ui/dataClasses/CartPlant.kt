package com.cotiledon.mobilApp.ui.dataClasses

data class CartPlant(val plantName: String,
                     val plantPrice: String,
                     val plantID : String,
                     val plantStock: String,
                     var plantQuantity: Int,
                     val plantImage: Int)