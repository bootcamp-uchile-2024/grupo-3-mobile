package com.cotiledon.mobilApp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.dataClasses.cart.CartPlant
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Locale

class CartSummaryAdapter(
    private val cartPlants: List<CartPlant>
) : RecyclerView.Adapter<CartSummaryAdapter.CartSummaryViewHolder>() {

    inner class CartSummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.product_image_shopping)
        val productName: TextView = itemView.findViewById(R.id.product_name_shopping)
        val productQuantity: TextView = itemView.findViewById(R.id.quantity_shopping)
        val productCurrentPrice: TextView = itemView.findViewById(R.id.current_price_shopping)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartSummaryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart_summary, parent, false)
        return CartSummaryViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CartSummaryViewHolder, position: Int) {
        val plant = cartPlants[position]

        holder.productName.text = plant.plantName
        holder.productQuantity.text = plant.plantQuantity.toString()
        holder.productCurrentPrice.text = formatPrice(plant.plantPrice.toDouble() * plant.plantQuantity)

        loadCartItemImage(plant.plantImage, holder.productImage, holder.itemView.context)
    }

    override fun getItemCount(): Int = cartPlants.size

    private fun formatPrice(price: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        return formatter.format(price)
    }

    private fun loadCartItemImage(imageUrl: String, imageView: ImageView, context: Context) {
        if (imageUrl.isBlank()) {
            Picasso.get()
                .load(R.drawable.suculenta)
                .into(imageView)
            return
        }

        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.user_24)
            .error(R.drawable.suculenta)
            .resize(400, 400)
            .centerInside()
            .into(imageView)
    }
}