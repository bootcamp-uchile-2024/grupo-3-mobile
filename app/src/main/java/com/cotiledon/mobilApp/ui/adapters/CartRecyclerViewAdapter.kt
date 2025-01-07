package com.cotiledon.mobilApp.ui.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.activities.MainContainerActivity
import com.cotiledon.mobilApp.ui.dataClasses.cart.CartPlant
import com.cotiledon.mobilApp.ui.managers.CartStorageManager
import com.cotiledon.mobilApp.ui.backend.GlobalValues
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

class CartRecyclerViewAdapter (private val cartPlants: MutableList<CartPlant>,
                               private val cartStorageManager: CartStorageManager,
                               private val onItemRemoved: () -> Unit,
                               private val scope: CoroutineScope
) :
    RecyclerView.Adapter<CartRecyclerViewAdapter.CartViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.item_shopping_cart,
            parent, false)
        return CartViewHolder(inflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val planta = cartPlants[position]

        loadCartItemImage(planta.plantImage, holder.productImage, holder.itemView.context)
        holder.productName.text = planta.plantName
        holder.productQuantity.text = planta.plantQuantity.toString()
        holder.productCurrentPrice.text = formatPrice(planta.plantPrice.toDouble())

        holder.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(holder.itemView.context, planta, position)
        }

        holder.decreaseButton.setOnClickListener {
            if (planta.plantQuantity > 1) {
                planta.plantQuantity--
                scope.launch {
                    cartStorageManager.updateProductQuantity(planta.plantId, planta.plantQuantity)
                }

                Toast.makeText(
                    holder.itemView.context,
                    "Cantidad de ${planta.plantName} reducida a ${planta.plantQuantity}",
                    Toast.LENGTH_SHORT
                ).show()

                notifyItemChanged(position)
                onItemRemoved()
                (holder.itemView.context as? MainContainerActivity)?.updateCartBadge()
            } else {
                showDeleteConfirmationDialog(holder.itemView.context, planta, position)
            }
        }

        holder.increaseButton.setOnClickListener {

            planta.plantQuantity++
            scope.launch {
                cartStorageManager.updateProductQuantity(planta.plantId, planta.plantQuantity)
            }

            Toast.makeText(
                holder.itemView.context,
                "Cantidad de ${planta.plantName} aumentada a ${planta.plantQuantity}",
                Toast.LENGTH_SHORT
            ).show()

            notifyItemChanged(position)
            onItemRemoved()
            (holder.itemView.context as? MainContainerActivity)?.updateCartBadge()

        }
    }

    private fun showDeleteConfirmationDialog(context: Context, plant: CartPlant, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Eliminar producto")
            .setMessage("¿Estás seguro de que deseas eliminar este producto del carrito?")
            .setPositiveButton("Eliminar") { dialog, _ ->
                // Launch coroutine for server delete
                scope.launch {
                    cartStorageManager.removeProductFromCart(plant.plantId)

                    // These operations should be done on the main thread
                    withContext(Dispatchers.Main) {
                        cartPlants.removeAt(position)
                        notifyItemRemoved(position)
                        onItemRemoved()

                        Toast.makeText(
                            context,
                            "${plant.plantName} ha sido eliminado del carrito",
                            Toast.LENGTH_SHORT
                        ).show()

                        (context as? MainContainerActivity)?.updateCartBadge()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    //Función para cargar imagenes con Picasso
    private fun loadCartItemImage(imageUrl: String, imageView: ImageView, context: Context) {
        if (imageUrl.isBlank()) {
            Picasso.get()
                .load(R.drawable.suculenta)
                .into(imageView)
            return
        }

        val completeUrl = if (imageUrl.startsWith("http")) {
            imageUrl
        } else {
            "${GlobalValues.backEndIP}${imageUrl}".trimEnd('/')
        }

        Picasso.get()
            .load(completeUrl)
            .apply {
                // Ajustar el tamaño de la imagen debido a que es solo de carrito
                resize(400, 400)
                centerInside()
                placeholder(R.drawable.user_24)
                error(R.drawable.suculenta)
            }
            .into(imageView, object : Callback {
                override fun onSuccess() {
                }

                override fun onError(e: Exception) {
                    Log.e("Error al cargar la imagen", e.message, e)
                }
            })
    }

    override fun getItemCount(): Int {
        return cartPlants.size
    }

    private fun formatPrice(price: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        return formatter.format(price)
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.product_image_shopping)
        val productName: TextView = itemView.findViewById(R.id.product_name_shopping)
        val productQuantity: TextView = itemView.findViewById(R.id.quantity_shopping)
        val productCurrentPrice: TextView = itemView.findViewById(R.id.current_price_shopping)
        val productDiscount: TextView = itemView.findViewById(R.id.discount_shopping)
        val productNormalPrice: TextView = itemView.findViewById(R.id.normal_price)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button_shopping)
        val decreaseButton: ImageButton = itemView.findViewById(R.id.btn_decrease_shopping)
        val increaseButton: ImageButton = itemView.findViewById(R.id.btn_increase_shopping)
    }


}