package com.cotiledon.mobilApp.ui.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.dataClasses.plant.Plant
import com.cotiledon.mobilApp.ui.backend.GlobalValues
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Locale


//Adaptador para la vista de catálogo que creamos ya que esta es un RecyclerView. Se le entrega la
// lista con objetos Plant y una variable que permitirá clickear en cada tarjeta

class PlantRecyclerViewAdapter(
    initialPlants: MutableList<Plant>,
    private val onItemClick: (Plant) -> Unit,
    private val onAddToCartClick: (Plant) -> Unit
) : FilterableAdapter<Plant,RecyclerView.ViewHolder>(initialPlants) {

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_LOADING = 1
    }

    private var isLoadingVisible = false
    private val failedImageLoads = mutableSetOf<String>()

    inner class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.iv_product)
        private val productName: TextView = itemView.findViewById(R.id.tv_product_name)
        private val productDescription: TextView = itemView.findViewById(R.id.tv_product_description)
        private val productPrice: TextView = itemView.findViewById(R.id.tv_product_price)
        private val addToCartButton: ImageButton = itemView.findViewById(R.id.btn_add_to_cart)
        private val petfriendlyIcon: ImageView = itemView.findViewById(R.id.pet_friendly_sign)

        @SuppressLint("SetTextI18n", "DefaultLocale")
        fun bind(plant: Plant) {
            productName.text = plant.nombre
            productDescription.text = plant.descripcion

            if (plant.planta?.petFriendly == false) {
                petfriendlyIcon.visibility = View.INVISIBLE
            }

            addToCartButton.setOnClickListener {
            onAddToCartClick(plant)
            }

            val formatter = NumberFormat.getNumberInstance(Locale.GERMAN)
            productPrice.text = "$ ${formatter.format(plant.precio)}"

            loadProductImage(plant, productImage)

            itemView.setOnClickListener { onItemClick(plant) }
            addToCartButton.setOnClickListener { onAddToCartClick(plant) }

        }
    }

    private fun loadProductImage(plant: Plant, imageView: ImageView) {
        val imageUrl = plant.imagenes.firstOrNull()?.let { imagen ->
            "${GlobalValues.backEndIP}${imagen.ruta.removePrefix("/")}"
        }

        if (!imageUrl.isNullOrEmpty()) {
            Picasso.get().cancelRequest(imageView)

            Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.user_24)
                .error(R.drawable.suculenta)
                .fit()
                .centerInside()
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        failedImageLoads.remove(imageUrl)
                    }

                    override fun onError(e: Exception) {
                        Log.e("PlantAdapter", "Error cargando la imagen: $imageUrl", e)
                        failedImageLoads.add(imageUrl)

                        imageView.postDelayed({
                            if (imageUrl in failedImageLoads) {
                                loadProductImage(plant, imageView)
                            }
                        }, 2000)
                    }
                })
        } else {
            imageView.setImageResource(R.drawable.suculenta)
        }
    }

    inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

    @SuppressLint("NotifyDataSetChanged")
    /*override fun filter(filters: PlantFilterParams) {
        filteredPlants = plants.filter { plant ->
            // Verificamos si la planta tiene detalles
            val plantDetails = plant.planta
            if (plantDetails == null) {
                false // Si no tiene detalles, no pasa el filtro
            } else {
                // Verificamos cada filtro
                val priceInRange = plant.precio.toFloat() in filters.priceRange

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
    }*/

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
                //Solo hacer binding si el item esta en la lista
                if (position < filteredItems.size) {
                    val plantHolder = holder as PlantViewHolder
                    val plant = filteredItems[position]
                    plantHolder.bind(plant)
                    holder.itemView.setOnClickListener {
                        onItemClick(plant)
                    }
                }
            }
            VIEW_TYPE_LOADING -> {
                //No se necesita binding para este ViewHolder
            }
        }
    }

    override fun getItemCount(): Int {
        return filteredItems.size + if (isLoadingVisible) 1 else 0
    }

    fun clearPlants() {
        clearItems()
        isLoadingVisible = false
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoadingVisible && position == filteredItems.size) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
    }


    fun updatePlants(newPlants: List<Plant>) {
        val startPosition = filteredItems.size
        filteredItems.addAll(newPlants)
        notifyItemRangeInserted(startPosition, newPlants.size)
        if (isLoadingVisible) {
            hideLoading()
        }
    }


    fun showLoading() {
        if (!isLoadingVisible) {
            isLoadingVisible = true
            notifyItemInserted(itemCount)
        }
    }

    fun hideLoading() {
        if (isLoadingVisible) {
            isLoadingVisible = false
            notifyItemRemoved(itemCount)
        }
    }

}