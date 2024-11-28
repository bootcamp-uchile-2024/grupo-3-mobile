package com.cotiledon.mobilApp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.dataClasses.Plant

class PlantRecyclerViewAdapter(
    private val plantsList: List<Plant>, // Lista de plantas
    private val onClick: (Plant) -> Unit // Callback para manejar clics en los ítems
) : RecyclerView.Adapter<PlantRecyclerViewAdapter.PlantViewHolder>() {

    // Clase ViewHolder que representa cada elemento del RecyclerView
    inner class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val plantImage: ImageView = itemView.findViewById(R.id.catalogCVImage)
        val plantName: TextView = itemView.findViewById(R.id.catalogCVName)
        val plantPrice: TextView = itemView.findViewById(R.id.catalogCVPrice)

        // Método para vincular los datos del modelo a las vistas
        fun bind(plant: Plant) {
            plantImage.setImageResource(plant.plantImage) // Imagen de la planta
            plantName.text = plant.plantName // Nombre de la planta
            plantPrice.text = "$${plant.plantPrice}" // Precio de la planta

            // Configurar el clic en el elemento
            itemView.setOnClickListener {
                onClick(plant)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_catalog, parent, false) // Infla tu layout
        return PlantViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = plantsList[position]
        holder.bind(plant) // Llama al método para vincular datos
    }

    override fun getItemCount(): Int {
        return plantsList.size // Devuelve el tamaño de la lista
    }
}
