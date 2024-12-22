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
import com.cotiledon.mobilApp.ui.dataClasses.PlantProductData
import com.cotiledon.mobilApp.ui.helpers.ProductDetailBarHelper
import com.cotiledon.mobilApp.ui.helpers.SearchBarHelper
import com.cotiledon.mobilApp.ui.managers.CartStorageManager
import com.cotiledon.mobilApp.ui.retrofit.GlobalValues
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.util.Locale

class ProductDetailFragment : Fragment() {

    private lateinit var productImage: ImageView
    private lateinit var productName: TextView
    private lateinit var currentPrice: TextView
    private lateinit var originalPrice: TextView
    private lateinit var discountPercentage: TextView
    private lateinit var descriptionText: TextView
    private lateinit var showCharacteristicsButton: Button
    private lateinit var ratingBar: RatingBar
    private lateinit var ratingScore: TextView
    private lateinit var reviewsRecyclerView: RecyclerView
    private lateinit var addToCartButton: Button
    private lateinit var previousImageButton: Button
    private lateinit var nextImageButton: Button
    private lateinit var decreaseQuantityButton: ImageButton
    private lateinit var increaseQuantityButton: ImageButton
    private lateinit var quantityTextView: TextView

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
        private const val ARG_PRODUCT_RATING = "product_rating"
        private const val ARG_PRODUCT_STOCK = "product_stock"

        //Factory del fragment
        fun newInstance(product: Plant): ProductDetailFragment {
            return ProductDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PRODUCT_ID, product.id)
                    putString(ARG_PRODUCT_NAME, product.nombre)
                    putString(ARG_PRODUCT_PRICE, product.precio.toString())
                    //TODO: Calcular el precio si hay descuento
                    product.planta?.let { plantDetails ->
                        // Add any additional plant-specific details here
                    }
                    //Manejo de imagenes del producto
                    putString(ARG_PRODUCT_DESCRIPTION, product.descripcion)
                    val imageUrls = product.imagenes.map {
                        "${GlobalValues.backEndIP}${it.ruta.removePrefix("/")}"
                    }
                    putStringArrayList(ARG_PRODUCT_IMAGES, ArrayList(imageUrls))
                    putInt(ARG_PRODUCT_STOCK, product.stock)
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

        //Setteo de clickeables
        setupClickListeners()

        //TODO: Setteo de recycler view
        //setupReviewsRecyclerView()
    }

    private fun initializeViews(view: View) {
        productImage = view.findViewById(R.id.productImage)
        productName = view.findViewById(R.id.productName)
        currentPrice = view.findViewById(R.id.currentPrice)
        originalPrice = view.findViewById(R.id.originalPrice)
        discountPercentage = view.findViewById(R.id.discountPercentage)
        descriptionText = view.findViewById(R.id.descriptionText)
        showCharacteristicsButton = view.findViewById(R.id.showCharacteristicsButton)
        ratingBar = view.findViewById(R.id.ratingBar)
        ratingScore = view.findViewById(R.id.ratingScore)
        reviewsRecyclerView = view.findViewById(R.id.reviewsRecyclerView)
        technicalDetailsContainer = view.findViewById(R.id.technical_details_container)
        previousImageButton = view.findViewById(R.id.image_back_btn)
        nextImageButton = view.findViewById(R.id.image_fwd_btn)
        decreaseQuantityButton = view.findViewById(R.id.btn_decrease)
        increaseQuantityButton = view.findViewById(R.id.btn_increase)
        quantityTextView = view.findViewById(R.id.quantity)
        addToCartButton = view.findViewById(R.id.add_to_cart_btn)
    }

    private fun setupClickListeners() {
        setupImageNavigation()
        setupQuantityControls()
        setupTechnicalDetailsButton()
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
        // Get maximum quantity from product stock
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
                // Show message when trying to exceed stock
                Toast.makeText(
                    context,
                    "No hay más stock disponible",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Set initial quantity display
        updateQuantityDisplay()
    }

    @SuppressLint("SetTextI18n")
    private fun updateQuantityDisplay() {
        quantityTextView.text = currentQuantity.toString()

        // Update button states based on quantity limits
        val maxStock = arguments?.getInt(ARG_PRODUCT_STOCK, 0) ?: 0
        decreaseQuantityButton.isEnabled = currentQuantity > 1
        increaseQuantityButton.isEnabled = currentQuantity < maxStock
    }

    private fun setupTechnicalDetailsButton() {
        showCharacteristicsButton.setOnClickListener {
            // Show technical details overlay with animation
            technicalDetailsContainer.apply {
                visibility = View.VISIBLE
                alpha = 0f
                animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start()
            }
        }

        // Set up back button in technical details view
        val backButton = technicalDetailsContainer.findViewById<ImageButton>(R.id.btn_back)
        backButton.setOnClickListener {
            // Hide technical details overlay with animation
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

            // Settear rating
            val rating = args.getFloat(ARG_PRODUCT_RATING, 5.0f)
            ratingBar.rating = rating
            ratingScore.text = String.format("%.1f", rating)
        }
    }