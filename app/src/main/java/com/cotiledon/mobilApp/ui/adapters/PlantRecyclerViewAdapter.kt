package com.cotiledon.mobilApp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.dataClasses.Plant


//Adaptador para la vista de catálogo que creamos ya que esta es un RecyclerView. Se le entrega la
// lista con objetos Plant y una variable que permitirá clickear en cada tarjeta

class PlantRecyclerViewAdapter(private val plants: List<Plant>, private val onItemClick: (Plant) -> Unit) :
    RecyclerView.Adapter<PlantRecyclerViewAdapter.PlantViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
            val inflater = LayoutInflater.from(parent.context).inflate(R.layout.catalog_view_card, parent, false)
            return PlantViewHolder(inflater)
        }
        //ViewHolder para la clase planta. Se poblan los datos de cada tarjeta y se define el onClickListener
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

    //Clase interna ViewHolder
    class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView: ImageView = itemView.findViewById(R.id.catalogCVImage)
        val tvName: TextView = itemView.findViewById(R.id.catalogCVName)
        val tvPrice: TextView = itemView.findViewById(R.id.catalogCVPrice)

    }

}