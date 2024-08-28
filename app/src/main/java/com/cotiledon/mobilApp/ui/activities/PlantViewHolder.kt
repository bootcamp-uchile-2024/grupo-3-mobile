package com.cotiledon.mobilApp.ui.activities

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R

class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val imageView: ImageView = itemView.findViewById(R.id.catalogCVImage)
    val tvName: TextView = itemView.findViewById(R.id.catalogCVName)
    val tvPrice: TextView = itemView.findViewById(R.id.catalogCVPrice)

}