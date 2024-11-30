package com.cotiledon.mobilApp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.dataClasses.Plant
import com.squareup.picasso.Picasso


//Adaptador para la vista de catálogo que creamos ya que esta es un RecyclerView. Se le entrega la
// lista con objetos Plant y una variable que permitirá clickear en cada tarjeta

class ProductRecyclerViewAdapter(
    private val plants: List<Plant>,
    private val onItemClick: (Plant) -> Unit
) : RecyclerView.Adapter<ProductRecyclerViewAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.catalog_view_card, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val plant = plants[position]
        holder.bind(plant)
        holder.itemView.setOnClickListener {
            onItemClick(plant)
        }
    }

    override fun getItemCount(): Int = plants.size

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.catalogCVImage)
        private val tvName: TextView = itemView.findViewById(R.id.catalogCVName)
        private val tvPrice: TextView = itemView.findViewById(R.id.catalogCVPrice)

        fun bind(product: Plant) {
            tvName.text = product.nombre
            tvPrice.text = "$ ${product.precio}"

            Picasso.get()
                .load(product.imagen)
                .placeholder(R.drawable.suculenta)
                .error(R.drawable.user_24)
                .into(imageView)
        }
    }
}