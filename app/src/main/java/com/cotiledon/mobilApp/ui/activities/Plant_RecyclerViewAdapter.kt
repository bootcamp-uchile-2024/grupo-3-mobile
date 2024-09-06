package com.cotiledon.mobilApp.ui.activities

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R

class PlantRecyclerViewAdapter( private val plants: List<Plant>) : RecyclerView.Adapter<PlantRecyclerViewAdapter.PlantViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val view: View = inflater.inflate(R.layout.catalog_view_card, parent, false)

            return PlantViewHolder(view)
        }

        override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
            holder.tvName.text = plants[position].plantName
            holder.tvPrice.text = plants[position].plantPrice
            holder.imageView.setImageResource(plants[position].plantImage)
        }

        override fun getItemCount(): Int = plants.size

    class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView: ImageView = itemView.findViewById(R.id.catalogCVImage)
        val tvName: TextView = itemView.findViewById(R.id.catalogCVName)
        val tvPrice: TextView = itemView.findViewById(R.id.catalogCVPrice)

    }

}