package com.cotiledon.mobilApp.ui.managers

import android.content.Context
import android.util.Log
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.activities.BaseActivity
import com.cotiledon.mobilApp.ui.dataClasses.cart.CartPlant
import com.cotiledon.mobilApp.ui.dataClasses.cart.CartProduct
import com.cotiledon.mobilApp.ui.dataClasses.cart.QueuedOperation
import com.cotiledon.mobilApp.ui.enums.CartOperation
import com.cotiledon.mobilApp.ui.backend.cart.RetrofitCartClient
import com.cotiledon.mobilApp.ui.fragments.SignInFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

//Clase de archivo de guardado de productos en el carrito
class CartStorageManager (private val context: Context, tokenManager: TokenManager) {
    //Archivo JSON para guardar los productos del carrito de manera local
    private val filename = "cart_data.json"
    //Se agregan variables para la comunicación con el servidor y coordinar con el ambiente local
    private val cartClient = RetrofitCartClient.createCartClient(tokenManager)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val queueKey = "cart_operation_queue"
    private val gson = Gson()

    //Variable para almacenar el ID del carrito traido desde el servidor
    private var serverCartId: Int? = null

    suspend fun getCartId(): Int? {
        if (serverCartId != null) {
            return serverCartId
        }

        try {
            // First try to get existing cart
            val response = cartClient.getUserCart(userId = 1) // TODO: Get actual user ID

            return when (response.code()) {
                200 -> {
                    val cart = response.body()
                    serverCartId = cart?.id
                    saveCartIdLocally(serverCartId)
                    serverCartId
                }
                404 -> {
                    // User has no cart, create a new one
                    val createResponse = cartClient.createCart(userId = 1) // TODO: Get actual user ID
                    when (createResponse.code()) {
                        201 -> {
                            val newCart = createResponse.body()
                            serverCartId = newCart?.id
                            saveCartIdLocally(serverCartId)
                            serverCartId
                        }
                        400 -> {
                            // User already has an active cart
                            Log.e("CartStorageManager", "User already has an active cart")
                            null
                        }
                        else -> null
                    }
                }
                else -> null
            }
        } catch (e: RetrofitCartClient.AuthenticationException) {
            // Handle authentication error
            handleAuthenticationError()
            null
        } catch (e: Exception) {
            Log.e("CartStorageManager", "Error getting/creating cart", e)
            null
        }
        return null
    }

    private fun handleAuthenticationError() {

        //TODO: Add authentication error handling
        //Navigate back to login
        //This requires some way to access the activity/navigation
        //You might want to use a callback or event bus for this
        (context as? BaseActivity)?.let { activity ->
            activity.runOnUiThread {
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SignInFragment())
                    .commit()
            }
        }
    }

    private fun saveCartIdLocally(cartId: Int?) {
        context.getSharedPreferences("cart_prefs", Context.MODE_PRIVATE)
            .edit()
            .putInt("server_cart_id", cartId ?: -1)
            .apply()
    }

    //Función para guardar un producto en el carrito
    suspend fun saveProductToCart(cartPlant: CartPlant) {
        try {
            //Guardamos localmente al inicio
            saveProductLocally(cartPlant)

            //Luego, intentamos guardar en el servidor
            val cartId = getCartId()

            if (cartId != null) {
                val response = cartClient.addProductToCart(
                    cartId = cartId,
                    productId = cartPlant.plantId.toInt(),
                    quantity = cartPlant.plantQuantity
                )

                if (!response.isSuccessful) {
                    //Si el producto no se pudo guardar en el servidor, lo dejamos en espera para volver a intentarlo en el futuro
                    queueForRetry(cartPlant)
                }

                else{
                    Log.d("CartStorageManager", "Producto guardado en el carrito de la API")
                }
            }

            else {
                queueForRetry(cartPlant)
            }

        } catch (e: Exception) {
            Log.e("CartStorageManager", "Error guardando el producto en el carrito", e)
            //De todas formas lo guardamos localmente en caso de que la sincronización falle
            saveProductLocally(cartPlant)
        }
    }

    private fun saveProductLocally(cartPlant: CartPlant){
        try {
            val existingData = readCartData()
            var productExists = false

            //Checkear si el producto existe en el carrito
            for (i in 0 until existingData.length()) {
                val item = existingData.getJSONObject(i)
                if (item.getString("plantID") == cartPlant.plantId) {
                    val currentQuantity = item.getInt("plantQuantity")
                    val newQuantity = currentQuantity + cartPlant.plantQuantity

                    val stockLimit = cartPlant.plantStock.toInt()
                    if (newQuantity <= stockLimit) {
                        item.put("plantQuantity", newQuantity)
                    } else {
                        item.put("plantQuantity", stockLimit)
                    }
                    productExists = true
                    break
                }
            }

            if (!productExists) {
                val productJson = JSONObject().apply {
                    put("plantName", cartPlant.plantName)
                    put("plantPrice", cartPlant.plantPrice)
                    put("plantID", cartPlant.plantId)
                    put("plantStock", cartPlant.plantStock)
                    put("plantQuantity", cartPlant.plantQuantity)
                    put("plantImage", cartPlant.plantImage)
                }
                existingData.put(productJson)
            }

            //Guardad datos de nuevo en el archivo
            context.openFileOutput(filename, Context.MODE_PRIVATE).use { output ->
                output.write(existingData.toString().toByteArray())
            }
        } catch (e: Exception) {
            Log.e("CartStorageManager", "Error guardando el producto localmente", e)
        }
    }

    suspend fun removeProductFromCart(plantId: String) {
        try {
            removeProductLocally(plantId)

            //Intentamos sincronizar con el servidor
            val cartId = getCartId()
            if (cartId != null) {

                val cartItems = loadCartItems()
                val productExists = cartItems.any { it.plantId == plantId }

                if (productExists) {
                    val response = cartClient.removeProductFromCart(
                        cartId = cartId,
                        productId = plantId.toInt()
                    )

                    if (!response.isSuccessful) {
                        //Si el producto no se pudo eliminar en el servidor, lo dejamos en espera para volver a intentarlo en el futuro
                        queueForDeletion(plantId)
                    }
                }

                else{
                    Log.d("CartStorageManager", "Producto eliminado en el carrito de la API")
                }
            }

            else {
                queueForDeletion(plantId)
            }

        } catch (e: Exception) {
            Log.e("CartStorageManager", "Error removiendo el producto del carrito", e)
            //De todas formas lo removemos localmente en caso de que la sincronización falle
            removeProductLocally(plantId)
    }
    }

    //Función para remover el producto localmente
    private fun removeProductLocally(plantId: String) {
        try {
            val jsonArray = readCartData()
            val updatedArray = JSONArray()

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                if (item.getString("plantID") != plantId) {
                    updatedArray.put(item)
                }
            }

            context.openFileOutput(filename, Context.MODE_PRIVATE).use { output ->
                output.write(updatedArray.toString().toByteArray())
            }
        } catch (e: Exception) {
            Log.e("CartStorageManager", "Error removiendo el producto localmente", e)
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

    fun cartItemsCount(): Int {
        return loadCartItems().sumOf { it.plantQuantity }
    }

    //Función para limpiar el carrito
    fun clearCart() {
        context.deleteFile(filename)
    }

    //Función para actualizar el carrito con el servidor
    suspend fun updateProductQuantity(plantId: String, newQuantity: Int) {
        try {
            updateQuantityLocally(plantId, newQuantity)

            val cartId = getCartId()
            if (cartId != null) {
                val cartProduct = CartProduct(
                    productoId = plantId.toInt(),
                    cantidadProducto = newQuantity
                )

                val response = cartClient.updateProductQuantity(
                    cartId = cartId,
                    cartProduct.productoId,
                    cartProduct.cantidadProducto
                )

                if (!response.isSuccessful) {
                    queueForUpdate(plantId, newQuantity)
                }

                else{
                    Log.d("CartStorageManager", "Producto actualizado en el carrito de la API")
                }
            }

            else {
                queueForUpdate(plantId, newQuantity)
            }

        } catch (e: Exception) {
            Log.e("CartStorageManager", "Error actualizando la cantidad", e)
            updateQuantityLocally(plantId, newQuantity)
        }
    }

    //Función para actualizar el carrito localmente
    private fun updateQuantityLocally(plantId: String, newQuantity: Int) {
        try{
            val jsonArray = readCartData()

            for(i in 0 until jsonArray.length()){
                val item = jsonArray.getJSONObject(i)
                if(item.getString("plantID") == plantId){
                    item.put("plantQuantity", newQuantity)
                    break
                }
            }

            context.openFileOutput(filename, Context.MODE_PRIVATE).use { output ->
                output.write(jsonArray.toString().toByteArray())
            }
        } catch (e: Exception) {
            Log.e("CartStorageManager", "Error actualizando la cantidad localmente", e)
        }
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

    private fun saveQueuedOperation(operation: QueuedOperation) {
        val prefs = context.getSharedPreferences("cart_prefs", Context.MODE_PRIVATE)
        val queueJson = prefs.getString(queueKey, "[]")
        val queueType = object : TypeToken<MutableList<QueuedOperation>>() {}.type
        val queue = gson.fromJson<MutableList<QueuedOperation>>(queueJson, queueType)

        // Add new operation
        queue.add(operation)

        // Save updated queue
        prefs.edit().putString(queueKey, gson.toJson(queue)).apply()

        // Process queue
        processQueue()
    }

    private fun processQueue() {
        scope.launch {
            try {
                val prefs = context.getSharedPreferences("cart_prefs", Context.MODE_PRIVATE)
                val queueJson = prefs.getString(queueKey, "[]")
                val queueType = object : TypeToken<MutableList<QueuedOperation>>() {}.type
                val queue = gson.fromJson<MutableList<QueuedOperation>>(queueJson, queueType)

                if (queue.isEmpty()) return@launch

                val tokenManager = TokenManager(context)
                val cartClient = RetrofitCartClient.createCartClient(tokenManager)
                val serverCartId = prefs.getInt("server_cart_id", -1)
                if (serverCartId == -1) return@launch

                val iterator = queue.iterator()
                while (iterator.hasNext()) {
                    val operation = iterator.next()
                    val success = try {
                        when (operation.operationType) {
                            CartOperation.ADD -> {
                                operation.cartPlant?.let { plant ->
                                    val response = cartClient.addProductToCart(
                                        cartId = serverCartId,
                                        productId = plant.plantId.toInt(),
                                        quantity = plant.plantQuantity
                                    )
                                    response.isSuccessful
                                } ?: false
                            }

                            CartOperation.UPDATE -> {
                                operation.quantity?.let { qty ->
                                    val response = cartClient.updateProductQuantity(
                                        cartId = serverCartId,
                                        productId = operation.plantId.toInt(),
                                        quantity = qty
                                    )
                                    response.isSuccessful
                                } ?: false
                            }

                            CartOperation.DELETE -> {
                                val response = cartClient.removeProductFromCart(
                                    cartId = serverCartId,
                                    productId = operation.plantId.toInt()
                                )
                                response.isSuccessful
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("CartStorageManager", "Error processing queued operation", e)
                        false
                    }

                    if (success) {
                        iterator.remove()
                    }
                }

                // Save updated queue
                prefs.edit().putString(queueKey, gson.toJson(queue)).apply()

            } catch (e: Exception) {
                Log.e("CartStorageManager", "Error processing queue", e)
            }
        }
    }

    private fun queueForRetry(cartPlant: CartPlant) {
        saveQueuedOperation(
            QueuedOperation(
                operationType = CartOperation.ADD,
                plantId = cartPlant.plantId,
                cartPlant = cartPlant
            )
        )
    }

    private fun queueForDeletion(plantId: String) {
        saveQueuedOperation(
            QueuedOperation(
                operationType = CartOperation.DELETE,
                plantId = plantId
            )
        )
    }

    private fun queueForUpdate(plantId: String, quantity: Int) {
        saveQueuedOperation(
            QueuedOperation(
                operationType = CartOperation.UPDATE,
                plantId = plantId,
                quantity = quantity
            )
        )
    }

}