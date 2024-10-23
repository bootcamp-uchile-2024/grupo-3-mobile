package com.cotiledon.mobilApp.ui.activities

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class CartStorage (private val context: Context) {
    private val filename = "cart_data.json"

    fun saveProductToCart(cartPlant: CartPlant){
    try {
        val productJson = JSONObject().apply {
            put("plantName", cartPlant.plantName)
            put("plantPrice", cartPlant.plantPrice)
            put("plantID", cartPlant.plantID)
            put("plantStock", cartPlant.plantStock)
            put("plantQuantity", cartPlant.plantQuantity)
            put("plantImage", cartPlant.plantImage)
        }

        val existingData = readCartData()
        existingData.put(productJson)

        context.openFileOutput(filename, Context.MODE_PRIVATE).use { output ->
            output.write(existingData.toString().toByteArray())
        }
    } catch (e: Exception){
        e.printStackTrace()
    }
    }

    private fun readCartData(): JSONArray {
        return try {
            if (context.fileList().contains(filename)) {
                val fileInputStream = context.openFileInput(filename)
                val size = fileInputStream.available()
                val buffer = ByteArray(size)
                fileInputStream.read(buffer)
                fileInputStream.close()
                val jsonString = String(buffer)
                JSONArray(jsonString)}
            else{
                JSONArray()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            JSONArray()
            }
            }

    fun loadCartItems(): List<CartPlant> {
        val cartItems = mutableListOf<CartPlant>()
        try{
            val jsonArray = readCartData()
            for (i in 0 until jsonArray.length()){
                val item = jsonArray.getJSONObject(i)
                cartItems.add(
                    CartPlant(
                        plantName = item.getString("plantName"),
                        plantPrice = item.getString("plantPrice"),
                        plantID = item.getString("plantID"),
                        plantStock = item.getString("plantStock"),
                        plantQuantity = item.getString("plantQuantity"),
                        plantImage = item.getInt("plantImage")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return cartItems
    }
}
