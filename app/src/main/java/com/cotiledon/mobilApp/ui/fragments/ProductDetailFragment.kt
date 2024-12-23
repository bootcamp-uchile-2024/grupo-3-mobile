package com.cotiledon.mobilApp.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Paint
import android.icu.text.NumberFormat
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.activities.MainContainerActivity
import com.cotiledon.mobilApp.ui.dataClasses.CartPlant
import com.cotiledon.mobilApp.ui.dataClasses.Plant
import com.cotiledon.mobilApp.ui.helpers.LeafRatingView
import com.cotiledon.mobilApp.ui.managers.CartStorageManager
import com.cotiledon.mobilApp.ui.retrofit.GlobalValues
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.util.Locale
import kotlin.math.roundToInt

class ProductDetailFragment : Fragment() {

    private lateinit var backButton: ImageButton
    private lateinit var productImage: ImageView
    private lateinit var productName: TextView
    private lateinit var currentPrice: TextView
    private lateinit var originalPrice: TextView
    private lateinit var discountPercentage: TextView
    private lateinit var descriptionText: TextView
    private lateinit var showCharacteristicsButton: Button
    private lateinit var leafRatingView: LeafRatingView
    private lateinit var ratingScore: TextView
    private lateinit var reviewsRecyclerView: RecyclerView
    private lateinit var addToCartButton: Button
    private lateinit var previousImageButton: Button
    private lateinit var nextImageButton: Button
    private lateinit var decreaseQuantityButton: ImageButton
    private lateinit var increaseQuantityButton: ImageButton
    private lateinit var quantityTextView: TextView
    //Declaraciones para la ficha técnica
    private lateinit var technicalDescriptionText: TextView
    private lateinit var lightRequirementText: TextView
    private lateinit var wateringText: TextView
    private lateinit var environmentText: TextView
    private lateinit var petFriendlyText: TextView
    private lateinit var speciesText: TextView
    private lateinit var lifecycleText: TextView
    private lateinit var growthText: TextView
    private lateinit var resistanceText: TextView

    //Container para overlay de datos técnicos
    private lateinit var technicalDetailsContainer: ConstraintLayout

    //Manager local de carrito
    private lateinit var cartManager: CartStorageManager

    private var currentImageIndex = 0
    private var productImages = mutableListOf<String>()
    private var currentQuantity = 1

    //Manejo de argumentos del fragment
    companion object {
        private const val ARG_PRODUCT_ID = "product_id"
        private const val ARG_PRODUCT_NAME = "product_name"
        private const val ARG_PRODUCT_PRICE = "product_price"
        private const val ARG_PRODUCT_ORIGINAL_PRICE = "product_original_price"
        private const val ARG_PRODUCT_DISCOUNT = "product_discount"
        private const val ARG_PRODUCT_DESCRIPTION = "product_description"
        private const val ARG_PRODUCT_IMAGES = "product_images"
        private const val ARG_PRODUCT_STOCK = "product_stock"
        private const val ARG_PRODUCT_RATING = "product_rating"
        //TODO: Backend y UX a definir uso de dimensiones
        private const val ARG_PRODUCT_HEIGHT = "product_height"
        private const val ARG_PRODUCT_WIDTH = "product_width"
        private const val ARG_PRODUCT_LENGTH = "product_length"
        private const val ARG_PRODUCT_WEIGHT = "product_weight"
        private const val ARG_PRODUCT_PET_FRIENDLY = "pet_friendly"
        private const val ARG_PRODUCT_CYCLE = "product_cycle"
        private const val ARG_PRODUCT_SPECIES = "product_species"
        private const val ARG_PRODUCT_GROWTH_HABIT = "product_growth_habit"
        private const val ARG_PRODUCT_COLOR = "product_color"
        private const val ARG_PRODUCT_PHOTO_PERIOD = "product_photo_period"
        private const val ARG_PRODUCT_IRRIGATION_TYPE = "product_irrigation_type"
        private const val ARG_PRODUCT_HABITAT = "product_habitat"
        private const val ARG_PRODUCT_LIGHTING = "product_lighting"
        private const val ARG_PRODUCT_TEMPERATURE = "product_temperature"
        private const val ARG_PRODUCT_SIZE = "product_size"



        //Factory del fragment
        fun newInstance(planta: Plant): ProductDetailFragment {
            return ProductDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PRODUCT_ID, planta.id)
                    putString(ARG_PRODUCT_NAME, planta.nombre)
                    putString(ARG_PRODUCT_PRICE, planta.precio.toString())
                    //TODO: Calcular el precio si hay descuento (back a implementar descuentos)
                    //Manejo de imbeded class de detalles de la planta
                    planta.planta?.let { plantDetails ->
                        putBoolean(ARG_PRODUCT_PET_FRIENDLY, plantDetails.petFriendly)
                        putBoolean(ARG_PRODUCT_CYCLE, plantDetails.ciclo)
                        putString(ARG_PRODUCT_SPECIES, plantDetails.especie)
                        putString(ARG_PRODUCT_GROWTH_HABIT, plantDetails.habitoCrecimiento)
                        putString(ARG_PRODUCT_COLOR, plantDetails.color)
                        putString(ARG_PRODUCT_PHOTO_PERIOD, plantDetails.fotoPeriodo)
                        putString(ARG_PRODUCT_IRRIGATION_TYPE, plantDetails.tipoRiego)
                        putString(ARG_PRODUCT_HABITAT, plantDetails.entorno)
                        putString(ARG_PRODUCT_LIGHTING, plantDetails.iluminacion)
                        putString(ARG_PRODUCT_TEMPERATURE, plantDetails.toleranciaTemperatura)
                        putString(ARG_PRODUCT_SIZE,plantDetails.tamano)
                    }
                    //Manejo de imagenes del producto
                    putString(ARG_PRODUCT_DESCRIPTION, planta.descripcion)
                    val imageUrls = planta.imagenes.map {
                        "${GlobalValues.backEndIP}${it.ruta.removePrefix("/")}"
                    }
                    putStringArrayList(ARG_PRODUCT_IMAGES, ArrayList(imageUrls))
                    putInt(ARG_PRODUCT_STOCK, planta.stock)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        cartManager = CartStorageManager(requireContext())
        setupProductDetails()

        val rating = arguments?.getFloat(ARG_PRODUCT_RATING, 5.0f) ?: 5.0f
        leafRatingView.setRating(rating)

        //Setteo de clickeables
        setupClickListeners()

        //TODO: Setteo de recycler view
        //setupReviewsRecyclerView()
    }

    private fun initializeViews(view: View) {
        backButton = view.findViewById(R.id.btn_back)
        productImage = view.findViewById(R.id.productImage)
        productName = view.findViewById(R.id.productName)
        currentPrice = view.findViewById(R.id.currentPrice)
        originalPrice = view.findViewById(R.id.originalPrice)
        discountPercentage = view.findViewById(R.id.discountPercentage)
        descriptionText = view.findViewById(R.id.technical_description_text)
        showCharacteristicsButton = view.findViewById(R.id.showCharacteristicsButton)
        leafRatingView = view.findViewById(R.id.leafRatingView)
        ratingScore = view.findViewById(R.id.ratingScore)
        reviewsRecyclerView = view.findViewById(R.id.reviewsRecyclerView)
        technicalDetailsContainer = view.findViewById(R.id.technical_details_container)
        previousImageButton = view.findViewById(R.id.image_back_btn)
        nextImageButton = view.findViewById(R.id.image_fwd_btn)
        decreaseQuantityButton = view.findViewById(R.id.btn_decrease)
        increaseQuantityButton = view.findViewById(R.id.btn_increase)
        quantityTextView = view.findViewById(R.id.quantity)
        addToCartButton = view.findViewById(R.id.add_to_cart_btn)
        technicalDetailsContainer.apply {
            technicalDescriptionText = findViewById(R.id.technical_description_text)
            lightRequirementText = findViewById(R.id.lightRequirementText)
            wateringText = findViewById(R.id.wateringText)
            environmentText = findViewById(R.id.environmentText)
            petFriendlyText = findViewById(R.id.petFriendlyText)
            speciesText = findViewById(R.id.speciesText)
            lifecycleText = findViewById(R.id.lifecycleText)
            growthText = findViewById(R.id.growthText)
            resistanceText = findViewById(R.id.resistanceText)
        }
        setupBackNavigation()
    }

    private fun setupClickListeners() {
        setupImageNavigation()
        setupQuantityControls()
        setupTechnicalDetailsButton()
        setupAddProductToCartButton()
    }

    private fun setupBackNavigation() {
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupImageNavigation() {
        //Cargar las imágenes del producto
        arguments?.getStringArrayList(ARG_PRODUCT_IMAGES)?.let { images ->
            productImages.clear()
            productImages.addAll(images)

            // Solo habilitamos los botones de navegación si hay más de una imagen
            if (productImages.size > 1) {
                previousImageButton.focusable = View.FOCUSABLE
                nextImageButton.focusable = View.FOCUSABLE
            } else {
                previousImageButton.focusable = View.NOT_FOCUSABLE
                nextImageButton.focusable = View.NOT_FOCUSABLE
                nextImageButton.alpha = 0.5f
                previousImageButton.alpha = 0.5f
            }

            // Cargamos la primera imagen
            if (productImages.isNotEmpty()) {
                loadProductImage(currentImageIndex)
            }
        }

        //Setteo de botones de navegación
        previousImageButton.setOnClickListener {
            if (currentImageIndex > 0) {
                currentImageIndex--
                loadProductImage(currentImageIndex)
                updateNavigationButtonsState()
            }
        }

        nextImageButton.setOnClickListener {
            if (currentImageIndex < productImages.size - 1) {
                currentImageIndex++
                loadProductImage(currentImageIndex)
                updateNavigationButtonsState()

            }
        }
    }

    private fun loadProductImage(index: Int) {
        if (index in productImages.indices) {
            Picasso.get()
                .load(productImages[index])
                .placeholder(R.drawable.user_24)
                .error(R.drawable.suculenta)
                .into(productImage, object : Callback {
                    override fun onSuccess() {
                        // La imagen se cargó exitosamente
                    }

                    override fun onError(e: Exception) {
                        Log.e("ProductDetailFragment", "Error loading image: ${e.message}")
                        // Podríamos mostrar un mensaje al usuario si lo consideramos necesario
                    }
                })
        }
    }

    private fun updateNavigationButtonsState() {
        previousImageButton.isEnabled = currentImageIndex > 0
        nextImageButton.isEnabled = currentImageIndex < productImages.size - 1

        // Aplicamos un alpha para indicar visualmente si el botón está habilitado
        previousImageButton.alpha = if (previousImageButton.isEnabled) 1.0f else 0.5f
        nextImageButton.alpha = if (nextImageButton.isEnabled) 1.0f else 0.5f
    }

    private fun setupQuantityControls() {
        val maxStock = arguments?.getInt(ARG_PRODUCT_STOCK, 0) ?: 0

        decreaseQuantityButton.setOnClickListener {
            if (currentQuantity > 1) {
                currentQuantity--
                updateQuantityDisplay()
            }
        }

        increaseQuantityButton.setOnClickListener {
            if (currentQuantity < maxStock) {
                currentQuantity++
                updateQuantityDisplay()
            } else {
                Toast.makeText(
                    requireContext(),
                    "No hay más stock disponible",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        updateQuantityDisplay()
    }

    private fun setupAddProductToCartButton(){
        addToCartButton.setOnClickListener {
            arguments?.let { args ->
                val stock = args.getInt(ARG_PRODUCT_STOCK, 0)

                if (stock >= currentQuantity) {
                    val cartProduct = CartPlant(
                        plantName = args.getString(ARG_PRODUCT_NAME) ?: "",
                        plantPrice = args.getString(ARG_PRODUCT_PRICE) ?: "",
                        plantId = args.getInt(ARG_PRODUCT_ID).toString(),
                        plantStock = stock.toString(),
                        plantQuantity = currentQuantity,
                        plantImage = args.getStringArrayList(ARG_PRODUCT_IMAGES)?.firstOrNull()
                            ?: ""
                    )

                    cartManager.saveProductToCart(cartProduct)

                    Toast.makeText(
                        requireContext(),
                        "${cartProduct.plantName} añadido al carrito",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Actualizamos el badge del carrito en la actividad principal
                    (activity as? MainContainerActivity)?.updateCartBadge()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Lo sentimos, ${args.getString(ARG_PRODUCT_NAME)} no tiene más stock",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateQuantityDisplay() {
        quantityTextView.text = currentQuantity.toString()

        val maxStock = arguments?.getInt(ARG_PRODUCT_STOCK, 0) ?: 0
        decreaseQuantityButton.isEnabled = currentQuantity > 1
        increaseQuantityButton.isEnabled = currentQuantity < maxStock
    }

    private fun setupTechnicalDetails() {
        arguments?.let { args ->
            technicalDescriptionText.text = args.getString(ARG_PRODUCT_DESCRIPTION, "")
            lightRequirementText.text = args.getString(ARG_PRODUCT_LIGHTING, "")
            wateringText.text = args.getString(ARG_PRODUCT_IRRIGATION_TYPE, "")
            environmentText.text = args.getString(ARG_PRODUCT_HABITAT, "")
            val isPetFriendly = args.getBoolean(ARG_PRODUCT_PET_FRIENDLY)
            petFriendlyText.text = if (isPetFriendly) "Sí" else "No"
            speciesText.text = args.getString(ARG_PRODUCT_SPECIES, "")
            val isCyclePerenne = args.getBoolean(ARG_PRODUCT_CYCLE)
            lifecycleText.text = if (isCyclePerenne) "Perenne" else "Anual"
            growthText.text = args.getString(ARG_PRODUCT_GROWTH_HABIT, "")
            resistanceText.text = args.getString(ARG_PRODUCT_TEMPERATURE, "")
        }
    }

    private fun setupTechnicalDetailsButton() {
        showCharacteristicsButton.setOnClickListener {

            setupTechnicalDetails()

            //Animanción para la visibilidad del contenedor
            technicalDetailsContainer.apply {
                visibility = View.VISIBLE
                alpha = 0f
                animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start()
            }
        }

        //Boton back para la vista de tecnicalDetailContainer
        val backButton = technicalDetailsContainer.findViewById<ImageButton>(R.id.btn_back)
        backButton.setOnClickListener {
            //Misma animación para esconder la visa de detalle
            technicalDetailsContainer.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    technicalDetailsContainer.visibility = View.GONE
                }
                .start()
        }
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun setupProductDetails() {
        arguments?.let { args ->
            //Cargar imágen de producto
            val imageUrl = args.getString(ARG_PRODUCT_IMAGES)
            if (!imageUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.user_24)
                    .error(R.drawable.suculenta)
                    .into(productImage)
            }
            //Establecer nombre de producto
            productName.text = args.getString(ARG_PRODUCT_NAME)

            //Establecer precio original y descuento
            val price = args.getString(ARG_PRODUCT_PRICE)
            val originalPriceValue = args.getString(ARG_PRODUCT_ORIGINAL_PRICE)
            val discount = args.getString(ARG_PRODUCT_DISCOUNT)

            //Formato de precios
            val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
            currentPrice.text = formatter.format(price?.toDoubleOrNull() ?: 0.0)

            //Si no existe descuento, mostrar el precio original y el porcentaje de descuento
            if (!discount.isNullOrEmpty()) {
                originalPrice.apply {
                    visibility = View.VISIBLE
                    if (originalPriceValue != null) {
                        text = formatter.format(originalPriceValue.toDoubleOrNull() ?: 0.0)
                    }
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }

                discountPercentage.apply {
                    visibility = View.VISIBLE
                    text = "${discount}%"
                }
            }
            else{
                originalPrice.visibility = View.INVISIBLE
                discountPercentage.visibility = View.INVISIBLE

            }            }

            //Settear descripción
            val args = requireArguments()
            descriptionText.text = args.getString(ARG_PRODUCT_DESCRIPTION)
        }

}