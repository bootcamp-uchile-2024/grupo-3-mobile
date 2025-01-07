package com.cotiledon.mobilApp.ui.dataClasses.cart

data class CartPlant(val plantName: String,
                     val plantPrice: String,
                     val plantId : String,
                     val plantStock: String,
                     var plantQuantity: Int,
                     val plantImage: String
)