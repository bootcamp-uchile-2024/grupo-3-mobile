package com.cotiledon.mobilApp.ui.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R

class CartRecyclerViewAdapter (private val cartPlants: List<CartPlant>) : RecyclerView.Adapter<CartRecyclerViewAdapter.CartViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.shopping_cart_item_layout, parent, false)
        return CartViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val planta = cartPlants[position]
        holder.productImage.setImageResource(planta.plantImage)
        holder.productName.text = planta.plantName
        holder.productQuantity.text = planta.plantQuantity.toString()
        holder.productPrice.text = planta.plantPrice
    }

    override fun getItemCount(): Int {
        return cartPlants.size
    }

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val productImage: ImageView = itemView.findViewById(R.id.shoppingCartProductImage)
        val productName: TextView = itemView.findViewById(R.id.shoppingCartProductName)
        val productQuantity: TextView = itemView.findViewById(R.id.shoppingCartProductQuantity)
        val productPrice: TextView = itemView.findViewById(R.id.shoppingCartProductPrice)
    }

}