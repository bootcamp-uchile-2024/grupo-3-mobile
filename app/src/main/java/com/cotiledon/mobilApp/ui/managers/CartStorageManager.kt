package com.cotiledon.mobilApp.ui.managers

import android.content.Context
import android.util.Log
import com.cotiledon.mobilApp.ui.dataClasses.CartPlant
import org.json.JSONArray
import org.json.JSONObject

//Clase de archivo de guardado de productos en el carrito
class CartStorageManager (private val context: Context) {
    //Archivo JSON para guardar los productos del carrito
    private val filename = "cart_data.json"

    //Función para guardar un producto en el carrito
    fun saveProductToCart(cartPlant: CartPlant){
        try {
            val productJson = JSONObject().apply {
                put("plantName", cartPlant.plantName)
                put("plantPrice", cartPlant.plantPrice)
                put("plantID", cartPlant.plantId)
                put("plantStock", cartPlant.plantStock)
                put("plantQuantity", cartPlant.plantQuantity)
                put("plantImage", cartPlant.plantImage)
            }

            //Leer los datos existentes del archivo JSON
            val existingData = readCartData()
            existingData.put(productJson)

            //Escribir los datos actualizados en el archivo JSON
            context.openFileOutput(filename, Context.MODE_PRIVATE).use { output ->
                output.write(existingData.toString().toByteArray())
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun removeProductFromCart(plantID: String) {
        try {
            val jsonArray = readCartData()
            val updatedArray = JSONArray()

            // Crear un nuevo JSONArray excluyendo el producto a eliminar
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                if (item.getString("plantID") != plantID) {
                    updatedArray.put(item)
                }
            }

            // Guardar el array actualizado
            context.openFileOutput(filename, Context.MODE_PRIVATE).use { output ->
                output.write(updatedArray.toString().toByteArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //Función para leer los datos del archivo JSON
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

    //Función para cargar los productos del carrito
    fun loadCartItems(): MutableList<CartPlant> {
        val cartItems = mutableListOf<CartPlant>()
        try{
            val jsonArray = readCartData()
            //Leer los datos del archivo JSON y agregarlos a la lista de productos del carrito
            for (i in 0 until jsonArray.length()){
                val item = jsonArray.getJSONObject(i)
                cartItems.add(
                    CartPlant(
                        plantName = item.getString("plantName"),
                        plantPrice = item.getString("plantPrice"),
                        plantId = item.getString("plantID"),
                        plantStock = item.getString("plantStock"),
                        plantQuantity = item.getInt("plantQuantity"),
                        plantImage = item.getString("plantImage")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return cartItems
    }

    //Función para limpiar el carrito
    fun clearCart() {
        context.deleteFile(filename)
    }

    //Función para obtener los precios de los productos en el carrito
    private fun getCartPrices(): List<Double> {
        val prices = mutableListOf<Double>()
        try {
            val jsonArray = readCartData()
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val priceQuantity = item.getInt("plantQuantity")
                val priceString = item.getString("plantPrice")
                    .replace("$", "")
                    .replace(".", "")
                    .trim()
                prices.add(priceString.toDouble()*priceQuantity)
            }
        } catch (e: Exception) {
            Log.e("CartStorageManager", "Error extracting prices", e)
        }
        return prices
    }

    //Función para obtener el precio total de los productos en el carrito
    fun getTotalCartPrice(): Double {
        return getCartPrices().sum()
    }

}