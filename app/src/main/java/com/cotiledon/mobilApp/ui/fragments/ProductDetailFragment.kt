package com.cotiledon.mobilApp.ui.fragments

import android.icu.text.NumberFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.dataClasses.Categoria
import com.cotiledon.mobilApp.ui.dataClasses.Plant
import com.cotiledon.mobilApp.ui.dataClasses.PlantDetails
import java.util.Locale

class ProductDetailFragment : Fragment() {
    //Muestra inicial de productos para añadir al carrito
    private var quantity = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI(view)
        loadProductDetails()
    }

    private fun setupUI(view: View) {
        // Back button configuration
        view.findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Favorite button configuration
        view.findViewById<ImageButton>(R.id.favoriteButton).setOnClickListener {
            toggleFavorite()
        }

        // Configure quantity controls with stock limit check
        view.findViewById<ImageButton>(R.id.decreaseButton).setOnClickListener {
            if (quantity > 1) {
                quantity--
                updateQuantityDisplay(view)
            }
        }

        view.findViewById<ImageButton>(R.id.increaseButton).setOnClickListener {
            // We'll set the maximum based on the plant's available stock
            if (quantity < (currentPlant?.cantidad ?: 0)) {
                quantity++
                updateQuantityDisplay(view)
            }
        }

        // Add to cart button configuration
        view.findViewById<Button>(R.id.addToCartButton).setOnClickListener {
            addToCart()
        }

        // Show characteristics button configuration
        view.findViewById<Button>(R.id.showCharacteristicsButton).setOnClickListener {
            showCharacteristicsDialog()
        }
    }

    private fun updateQuantityDisplay(view: View) {
        view.findViewById<TextView>(R.id.quantityText).text = quantity.toString()
    }

    // Keep track of the current plant for stock management
    private var currentPlant: Plant? = null

    private fun loadProductDetails() {
        // This is an example. In a real app, you would load from your repository
        val plant = Plant(
            id = 1,
            SKU = "STR-001",
            nombre = "Stromanthe",
            idCategoria = 1,
            precio = 11693.0,
            descripcion = "La Stomathe Es Una Planta De Interior Originaria De Las Exuberantes Selvas Tropicales...",
            imagen = "stromanthe_image.jpg",
            cantidad = 10,  // Available stock
            unidadesVendidas = 5,
            puntuacion = 5,
            ancho = 20,
            alto = 40,
            largo = 20,
            peso = 2,
            habilitado = true,
            categoria = Categoria(1, "Plantas de Interior"),
            planta = PlantDetails(
                idProducto = 1,
                petFriendly = true,
                toleranciaTemperatura = 25,
                ciclo = true,  // true for perennial
                altura = "40-60 cm",
                idEspecie = 1,
                idColor = 1,
                idFotoperiodo = 1,
                idTipoRiego = 1,
                idHabitoCrecimiento = 1,
                habitoCrecimiento = "Arbustivo",
                especie = "Stromanthe sanguinea",
                color = "Verde/Rosado",
                fotoPeriodo = "Sol directo",
                tipoRiego = "Riego manual"
            )
        )

        currentPlant = plant
        displayPlantDetails(plant)
    }

    // Add a formatter property at class level so we don't recreate it every time
    private val priceFormatter = NumberFormat.getNumberInstance(Locale.GERMAN)

    private fun formatPrice(price: Double): String {
        // Helper function to format prices consistently throughout the fragment
        return "$ ${priceFormatter.format(price)}"
    }

    private fun displayPlantDetails(plant: Plant) {
        view?.let { view ->
            // Basic product information
            view.findViewById<TextView>(R.id.productName).text = plant.nombre
            view.findViewById<TextView>(R.id.currentPrice).text = formatPrice(plant.precio)
            view.findViewById<TextView>(R.id.descriptionText).text = plant.descripcion

            // Load product image if available
            plant.imagen?.let { imageUrl ->
                val imageView = view.findViewById<ImageView>(R.id.productImage)
                // Use your image loading library here (Glide, Picasso, etc.)
                // For example with Glide:
                // Glide.with(this).load(imageUrl).into(imageView)
            }

            // Display plant details if available
            plant.planta?.let { details ->
                // Technical Details Section
                view.findViewById<TextView>(R.id.petFriendlyText).text =
                    if (details.petFriendly) "Sí" else "No"

                // Characteristics Section
                view.findViewById<TextView>(R.id.speciesText).text =
                    details.especie

                // Additional plant information could be displayed if needed
                // For example, ratings, units sold, etc.
            }

            // Update stock information
            updateStockStatus(plant.cantidad)
        }
    }

    private fun updateStockStatus(stock: Int) {
        view?.let { view ->
            // Enable/disable increase button based on stock
            view.findViewById<ImageButton>(R.id.increaseButton).isEnabled = quantity < stock

            // Update add to cart button state
            view.findViewById<Button>(R.id.addToCartButton).isEnabled = stock > 0
        }
    }

    private fun toggleFavorite() {
        // Implement favorite toggling logic
    }

    private fun addToCart() {
        currentPlant?.let { plant ->
            if (quantity <= plant.cantidad) {
                // Implement add to cart logic
                // You can use the plant.SKU and quantity here
            }
        }
    }

    private fun showCharacteristicsDialog() {
        // Implement characteristics dialog display
        currentPlant?.planta?.let { details ->
            // Create and show your characteristics dialog
        }
    }
}