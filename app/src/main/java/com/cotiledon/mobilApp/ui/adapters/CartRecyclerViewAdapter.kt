package com.cotiledon.mobilApp.ui.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.dataClasses.CartPlant
import com.cotiledon.mobilApp.ui.managers.CartStorageManager
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Locale

class CartRecyclerViewAdapter (private val cartPlants: MutableList<CartPlant>,
                               private val cartStorageManager: CartStorageManager,
                               private val onItemRemoved: () -> Unit) :
    RecyclerView.Adapter<CartRecyclerViewAdapter.CartViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.fragment_shopping_cart_item_layout,
            parent, false)
        return CartViewHolder(inflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val planta = cartPlants[position]

        loadCartItemImage(planta.plantImage, holder.productImage, holder.itemView.context)
        // TODO: Cargar el resto de los datos (ojo con descuento; revisar lógica del backend para manejo de este dato)
        holder.productName.text = planta.plantName
        holder.productQuantity.text = planta.plantQuantity.toString()
        holder.productCurrentPrice.text = formatPrice(planta.plantPrice.toDouble() * planta.plantQuantity)

        holder.deleteButton.setOnClickListener {
            // Mostrar diálogo de confirmación
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Eliminar producto")
                .setMessage("¿Estás seguro de que deseas eliminar este producto del carrito?")
                .setPositiveButton("Eliminar") { dialog, _ ->
                    // Eliminar el producto del almacenamiento
                    cartStorageManager.removeProductFromCart(planta.plantId)

                    // Eliminar el producto de la lista local
                    cartPlants.removeAt(position)
                    notifyItemRemoved(position)

                    // Notificar cambios para actualizar totales
                    onItemRemoved()

                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        holder.decreaseButton.setOnClickListener {
            if (planta.plantQuantity > 1) {
                planta.plantQuantity--
                cartStorageManager.saveProductToCart(planta)
                notifyItemChanged(position)
                onItemRemoved()
            }
        }

        holder.increaseButton.setOnClickListener {
            if (planta.plantQuantity < planta.plantStock.toInt()) {
                planta.plantQuantity++
                cartStorageManager.saveProductToCart(planta)
                notifyItemChanged(position)
                onItemRemoved()
            }
        }
    }

    // Helper function to handle image loading with Picasso
    private fun loadCartItemImage(imageUrl: String, imageView: ImageView, context: Context) {
        // First, check if we have a valid URL
        if (imageUrl.isBlank()) {
            // If no valid URL, load a placeholder
            Picasso.get()
                .load(R.drawable.suculenta)
                .into(imageView)
            return
        }

        // Load the actual image using Picasso
        Picasso.get()
            .load(imageUrl)
            .apply {
                // Since this is a cart item, we probably want smaller images
                // Adjust these dimensions based on your cart item layout
                resize(400, 400)
                centerInside()

                // Show a placeholder while loading
                placeholder(R.drawable.user_24)

                // Show an error image if loading fails
                error(R.drawable.suculenta)
            }
            .into(imageView, object : Callback {
                override fun onSuccess() {
                    // Image loaded successfully
                    // You could add animation or other UI updates here
                }

                override fun onError(e: Exception) {
                    // Handle error case - maybe show a toast or log the error
                    Toast.makeText(
                        context,
                        "Error al cargar la imagen",
                        Toast.LENGTH_SHORT
                    ).show()
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
        val increaseButton: ImageButton = itemView.findViewById(R.id.btn_increase)
    }


}