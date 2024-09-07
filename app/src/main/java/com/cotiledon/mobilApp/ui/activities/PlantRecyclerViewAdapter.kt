package com.cotiledon.mobilApp.ui.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R

class PlantRecyclerViewAdapter( private val plants: List<Plant>, private val onItemClick: (Plant) -> Unit) : RecyclerView.Adapter<PlantRecyclerViewAdapter.PlantViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
            val inflater = LayoutInflater.from(parent.context).inflate(R.layout.catalog_view_card, parent, false)
            return PlantViewHolder(inflater)
        }

        override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
            val planta = plants[position]
            holder.tvName.text = planta.plantName
            holder.tvPrice.text = planta.plantPrice
            holder.imageView.setImageResource(planta.plantImage)

            holder.itemView.setOnClickListener{
                onItemClick(planta)
            }

        }

        override fun getItemCount(): Int = plants.size

    class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView: ImageView = itemView.findViewById(R.id.catalogCVImage)
        val tvName: TextView = itemView.findViewById(R.id.catalogCVName)
        val tvPrice: TextView = itemView.findViewById(R.id.catalogCVPrice)

    }

}