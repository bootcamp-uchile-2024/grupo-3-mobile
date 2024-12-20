package com.cotiledon.mobilApp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.dataClasses.Plant
import com.cotiledon.mobilApp.ui.dataClasses.PlantFilters
import com.cotiledon.mobilApp.ui.enums.PlantCycle
import com.cotiledon.mobilApp.ui.retrofit.GlobalValues
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Locale


//Adaptador para la vista de catálogo que creamos ya que esta es un RecyclerView. Se le entrega la
// lista con objetos Plant y una variable que permitirá clickear en cada tarjeta

class PlantRecyclerViewAdapter(
    private val plants: MutableList<Plant>,
    private val onItemClick: (Plant) -> Unit,
    private val onAddToCartClick: (Plant) -> Unit
) : FilterableAdapter<Plant,RecyclerView.ViewHolder>(plants) {

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1

        private val TEMPERATURE_VALUES = mapOf(
            "Frío" to 1f,
            "Templado" to 2f,
            "Cálido" to 3f
        )
    }

    private var isLoadingVisible = false
    private var filteredPlants: MutableList<Plant> = plants

    inner class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.iv_product)
        val productName: TextView = itemView.findViewById(R.id.tv_product_name)
        val productDescription: TextView = itemView.findViewById(R.id.tv_product_description)
        val productPrice: TextView = itemView.findViewById(R.id.tv_product_price)
        val addToCartButton: ImageButton = itemView.findViewById(R.id.btn_add_to_cart)

        @SuppressLint("SetTextI18n", "DefaultLocale")
        fun bind(plant: Plant) {
            // Set basic product information
            productName.text = plant.nombre
            productDescription.text = plant.descripcion

            addToCartButton.setOnClickListener {
            onAddToCartClick(plant)
            }


            // Format price using the existing NumberFormat
            val formatter = NumberFormat.getNumberInstance(Locale.GERMAN)
            productPrice.text = "$ ${formatter.format(plant.precio)}"

            // Handle image loading - now using the first image from the array
            val imageUrl = plant.imagenes.firstOrNull()?.let {
                GlobalValues.backEndIP + it.ruta
            }

            Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.suculenta)
                .error(R.drawable.suculenta)
                .into(productImage)

            // Set click listeners
            itemView.setOnClickListener { onItemClick(plant) }
            addToCartButton.setOnClickListener { onAddToCartClick(plant) }
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

                // Height is now handled through the tamano field
                val heightInRange = when (plantDetails.tamano) {
                    "S" -> filters.heightRange.contains(25f)
                    "M" -> filters.heightRange.contains(50f)
                    "L" -> filters.heightRange.contains(75f)
                    "XL" -> filters.heightRange.contains(100f)
                    else -> true
                }

                val cycleMatches = filters.cycle == null ||
                        (filters.cycle == PlantCycle.ANNUAL && !plantDetails.ciclo) ||
                        (filters.cycle == PlantCycle.PERENNIAL && plantDetails.ciclo)

                val petFriendlyMatches = filters.isPetFriendly == null ||
                        plantDetails.petFriendly == filters.isPetFriendly

                val temperatureValue = TEMPERATURE_VALUES[plantDetails.toleranciaTemperatura] ?: 2f
                val temperatureMatches = temperatureValue in filters.temperatureRange

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
                    .inflate(R.layout.item_product, parent, false)
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