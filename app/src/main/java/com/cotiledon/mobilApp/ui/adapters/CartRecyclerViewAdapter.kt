package com.cotiledon.mobilApp.ui.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.dataClasses.CartPlant
import com.cotiledon.mobilApp.ui.managers.CartStorage
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CartRecyclerViewAdapter (private val cartPlants: MutableList<CartPlant>, private val cartStorage: CartStorage,
                               private val onItemRemoved: () -> Unit) :
    RecyclerView.Adapter<CartRecyclerViewAdapter.CartViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.shopping_cart_item_layout,
            parent, false)
        return CartViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val planta = cartPlants[position]
        holder.productImage.setImageResource(planta.plantImage)
        holder.productName.text = planta.plantName
        holder.productQuantity.text = planta.plantQuantity.toString()
        holder.productPrice.text = planta.plantPrice

        holder.deleteButton.setOnClickListener {
            // Mostrar diálogo de confirmación
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Eliminar producto")
                .setMessage("¿Estás seguro de que deseas eliminar este producto del carrito?")
                .setPositiveButton("Eliminar") { dialog, _ ->
                    // Eliminar el producto del almacenamiento
                    cartStorage.removeProductFromCart(planta.plantID)

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
    }

    override fun getItemCount(): Int {
        return cartPlants.size
    }

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val productImage: ImageView = itemView.findViewById(R.id.shoppingCartProductImage)
        val productName: TextView = itemView.findViewById(R.id.shoppingCartProductName)
        val productQuantity: TextView = itemView.findViewById(R.id.shoppingCartProductQuantity)
        val productPrice: TextView = itemView.findViewById(R.id.shoppingCartProductPrice)
        val deleteButton: Button = itemView.findViewById(R.id.shoppingCartDeleteButton)
    }

}