package com.cotiledon.mobilApp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.dataClasses.Plant
import com.cotiledon.mobilApp.ui.dataClasses.PlantFilters
import com.cotiledon.mobilApp.ui.enums.PlantCycle
import com.squareup.picasso.Picasso


//Adaptador para la vista de catálogo que creamos ya que esta es un RecyclerView. Se le entrega la
// lista con objetos Plant y una variable que permitirá clickear en cada tarjeta

class PlantRecyclerViewAdapter(
    private val plants: MutableList<Plant>,
    private val onItemClick: (Plant) -> Unit
) : FilterableAdapter<Plant,RecyclerView.ViewHolder>(plants) {

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }

    private var isLoadingVisible = false
    private var filteredPlants: MutableList<Plant> = plants

    inner class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.catalogCVImage)
        val tvName: TextView = itemView.findViewById(R.id.catalogCVName)
        val tvPrice: TextView = itemView.findViewById(R.id.catalogCVPrice)

        @SuppressLint("SetTextI18n")
        fun bind(plant: Plant) {
            tvName.text = plant.nombre
            tvPrice.text = "$ ${plant.precio}"

            Picasso.get()
                .load(plant.imagen)
                .placeholder(R.drawable.suculenta)
                .error(R.drawable.user_24)
                .into(imageView)
        }
    }

    inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

    @SuppressLint("NotifyDataSetChanged")
    override fun filter(filters: PlantFilters) {
        filteredPlants = plants.filter { plant ->
            // Verificamos si la planta tiene detalles
            val plantDetails = plant.planta
            if (plantDetails == null) {
                false // Si no tiene detalles, no pasa el filtro
            } else {
                // Verificamos cada filtro
                val priceInRange = plant.precio.toFloat() in filters.priceRange

                val heightInRange = try {
                    val heightValue = plantDetails.altura.replace(Regex("[^0-9]"), "").toFloat()
                    heightValue in filters.heightRange
                } catch (e: Exception) {
                    true // Si hay error al parsear la altura, no aplicamos este filtro
                }

                val cycleMatches = filters.cycle == null ||
                        (filters.cycle == PlantCycle.ANNUAL && !plantDetails.ciclo) ||
                        (filters.cycle == PlantCycle.PERENNIAL && plantDetails.ciclo)

                val petFriendlyMatches = filters.isPetFriendly == null ||
                        plantDetails.petFriendly == filters.isPetFriendly

                val temperatureMatches = plantDetails.toleranciaTemperatura.toFloat() in filters.temperatureRange

                val irrigationMatches = filters.irrigationType == null ||
                        plantDetails.tipoRiego.equals(filters.irrigationType.toString(), ignoreCase = true)

                // Combinamos todas las condiciones
                priceInRange && heightInRange && cycleMatches &&
                        petFriendlyMatches && temperatureMatches && irrigationMatches
            }
        }.toMutableList()

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.catalog_view_card, parent, false)
                PlantViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.progress_bar, parent, false)
                LoadingViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_ITEM -> {
                val plantHolder = holder as PlantViewHolder
                val plant = plants[position]
                plantHolder.bind(plant)
                holder.itemView.setOnClickListener {
                    onItemClick(plant)
                }
            }
            VIEW_TYPE_LOADING -> {
                // Loading view holder doesn't need any binding
            }
        }
    }

    override fun getItemCount(): Int = if (isLoadingVisible) filteredPlants.size + 1 else filteredPlants.size

    override fun getItemViewType(position: Int): Int {
        return if (position == filteredPlants.size && isLoadingVisible) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    fun showLoading() {
        if (!isLoadingVisible) {
            isLoadingVisible = true
            notifyItemInserted(filteredPlants.size)
        }
    }

    fun hideLoading() {
        if (isLoadingVisible) {
            isLoadingVisible = false
            notifyItemRemoved(filteredPlants.size)
        }
    }

    fun updatePlants(newPlants: List<Plant>) {
        plants.clear()
        plants.addAll(newPlants)
        filter(currentFilters)
    }

}