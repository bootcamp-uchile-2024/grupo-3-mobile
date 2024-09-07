package com.cotiledon.mobilApp.ui.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R


//Definimos el adaptador para la vista de catálogo que creamos ya que esta es un RecyclerView. Se le entrega la lista con objetos Plant y una variable que permitirá clickear en cada tarjeta
//El adaptador hereda del Adapter general de RecyclerView con el ViewHolder que definimos en nuestro adaptador
class PlantRecyclerViewAdapter( private val plants: List<Plant>, private val onItemClick: (Plant) -> Unit) : RecyclerView.Adapter<PlantRecyclerViewAdapter.PlantViewHolder>() {

        //Se define la función OnCreateViewHolder para generar cada tarjeta con los datos del ViewHolder que creamos
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
            val inflater = LayoutInflater.from(parent.context).inflate(R.layout.catalog_view_card, parent, false)
            return PlantViewHolder(inflater)
        }

        //Se definen los datos que cada tarjeta tendrá con esta función, definiendo los "holders" de nuestro ViewHolder con los datos de la clase Plant
        override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
            val planta = plants[position]
            holder.tvName.text = planta.plantName
            holder.tvPrice.text = planta.plantPrice
            holder.imageView.setImageResource(planta.plantImage)

            holder.itemView.setOnClickListener{
                onItemClick(planta)
            }

        }

        //Se cuenta la cantidad de items en el array plants para que el RecyclerView sepa cuantos objetos crear
        override fun getItemCount(): Int = plants.size

    //Se crea la clase interna de ViewHolder que permitirá , manejar la data proveniente del layout de CardView que se hizo para mostrar cada tarjeta en el catálogo
    class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView: ImageView = itemView.findViewById(R.id.catalogCVImage)
        val tvName: TextView = itemView.findViewById(R.id.catalogCVName)
        val tvPrice: TextView = itemView.findViewById(R.id.catalogCVPrice)

    }

}