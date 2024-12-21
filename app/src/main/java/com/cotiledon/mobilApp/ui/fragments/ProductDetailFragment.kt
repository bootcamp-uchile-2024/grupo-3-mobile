package com.cotiledon.mobilApp.ui.fragments

import android.annotation.SuppressLint
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
import android.widget.Toast
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.activities.MainContainerActivity
import com.cotiledon.mobilApp.ui.dataClasses.CartPlant
import com.cotiledon.mobilApp.ui.dataClasses.PlantProductData
import com.cotiledon.mobilApp.ui.helpers.ProductDetailBarHelper
import com.cotiledon.mobilApp.ui.helpers.SearchBarHelper
import com.cotiledon.mobilApp.ui.managers.CartStorageManager
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.util.Locale

class ProductDetailFragment : Fragment(), SearchBarHelper.SearchCallback {
    private var quantity = 1
    private lateinit var productDetailBarHelper: ProductDetailBarHelper
    private lateinit var cartStorageManager: CartStorageManager
    private var currentPlant: PlantProductData? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Inicializar los helpers
        productDetailBarHelper = ProductDetailBarHelper(this, this)
        cartStorageManager = CartStorageManager(requireContext())

        productDetailBarHelper.setHint("Buscar")

        //Inicializar CartManager
        cartStorageManager = CartStorageManager(requireContext())

        // Get data from arguments and load product details
        arguments?.let { args ->
            currentPlant = extractPlantDataFromArguments(args)
            currentPlant?.let { displayPlantDetails(it) }
        } ?: run {
            // Handle the case where no arguments were provided
            Toast.makeText(
                requireContext(),
                "Error: No product details provided",
                Toast.LENGTH_SHORT
            ).show()
            parentFragmentManager.popBackStack()
        }

        setupClickListeners()
    }

    private fun extractPlantDataFromArguments(args: Bundle): PlantProductData {
        return PlantProductData(
            // We use the Elvis operator (?:) to provide default values in case the arguments are missing
            id = args.getString(ARG_PLANT_ID)?.toIntOrNull() ?: -1,
            sku = args.getString(ARG_PLANT_SKU) ?: "",
            name = args.getString(ARG_PLANT_NAME) ?: "",
            price = args.getString(ARG_PLANT_PRICE)?.toDoubleOrNull() ?: 0.0,
            description = args.getString(ARG_PLANT_DESC) ?: "",
            image = args.getString(ARG_PLANT_IMAGE) ?: "",
            stock = args.getString(ARG_PLANT_STOCK)?.toIntOrNull() ?: 0,
            unitsSold = args.getString(ARG_PLANT_UNITS_SOLD)?.toIntOrNull() ?: 0,
            rating = args.getString(ARG_PLANT_RATING)?.toIntOrNull() ?: 0,
            width = args.getString(ARG_PLANT_WIDTH)?.toIntOrNull() ?: 0,
            height = args.getString(ARG_PLANT_HEIGHT)?.toIntOrNull() ?: 0,
            length = args.getString(ARG_PLANT_LENGTH)?.toIntOrNull() ?: 0,
            weight = args.getString(ARG_PLANT_WEIGHT)?.toIntOrNull() ?: 0,
            enabled = args.getBoolean(ARG_PLANT_ENABLED, true),
            categoryId = args.getString(ARG_PLANT_CATEGORY_ID)?.toIntOrNull() ?: -1
        )
    }

    @SuppressLint("SetTextI18n")
    private fun displayPlantDetails(plantData: PlantProductData) {
        view?.let { view ->
            // Basic information
            view.findViewById<TextView>(R.id.productName).text = plantData.name
            view.findViewById<TextView>(R.id.descriptionText).text = plantData.description

            // Format and display price
            val formatter = NumberFormat.getNumberInstance(Locale.GERMAN)
            view.findViewById<TextView>(R.id.currentPrice).text =
                "$ ${formatter.format(plantData.price)}"

            // Additional information (you might need to add these views to your layout)
            //view.findViewById<TextView>(R.id.productSKU)?.text = "SKU: ${plantData.sku}"
            //view.findViewById<TextView>(R.id.productRating)?.text =
                //"Rating: ${plantData.rating}/5"
            //view.findViewById<TextView>(R.id.productUnitsSold)?.text =
                //"Units Sold: ${plantData.unitsSold}"

            // Dimensions (if you have these fields in your layout)
            //view.findViewById<TextView>(R.id.productDimensions)?.text =
                //"Dimensions: ${plantData.width}x${plantData.height}x${plantData.length} cm"
            //view.findViewById<TextView>(R.id.productWeight)?.text =
                //"Weight: ${plantData.weight} kg"

            // Load product image
            loadProductImage(plantData.image, view.findViewById(R.id.productImage))

            // Update stock information
            updateStockStatus(plantData.stock)
        }
    }

    private fun loadProductImage(imageUrl: String, imageView: ImageView) {
        if (imageUrl.isBlank()) {
            Picasso.get()
                .load(R.drawable.suculenta)
                .into(imageView)
            return
        }

        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.user_24)
            .error(R.drawable.suculenta)
            .resize(800, 800)
            .centerInside()
            .into(imageView, object : Callback {
                override fun onSuccess() {
                    // Image loaded successfully
                }

                override fun onError(e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Error loading image: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun setupClickListeners() {
        view?.let { view ->

            // Quantity controls
            setupQuantityControls(view)

            // Add to cart button
            view.findViewById<Button>(R.id.addToCartButton).setOnClickListener {
                addToCart()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupQuantityControls(view: View) {
        val quantityDisplay = view.findViewById<TextView>(R.id.quantityText)

        view.findViewById<ImageButton>(R.id.decreaseButton).setOnClickListener {
            if (quantity > 1) {
                quantity--
                quantityDisplay.text = quantity.toString()
            }
        }

        view.findViewById<ImageButton>(R.id.increaseButton).setOnClickListener {
            currentPlant?.let { plant ->
                if (quantity < plant.stock) {
                    quantity++
                    quantityDisplay.text = quantity.toString()
                }
            }
        }
    }

    // Implement SearchCallback interface methods
    override fun onQueryTextSubmit(query: String) {
        // Handle search submission if needed
    }

    override fun onQueryTextChange(newText: String) {
        // Handle search text changes if needed
    }

    override fun onCameraButtonClick() {
        // Handle camera button clicks if needed
    }

    private fun addToCart() {
        currentPlant?.let { plant ->
            if (quantity <= plant.stock) {
                val cartPlant = CartPlant(
                    plantName = plant.name,
                    plantPrice = plant.price.toString(),
                    plantId = plant.id.toString(),
                    plantStock = plant.stock.toString(),
                    plantQuantity = quantity,
                    plantImage = plant.image
                )
                cartStorageManager.saveProductToCart(cartPlant)

                Toast.makeText(
                    requireContext(),
                    "${plant.name} added to cart successfully",
                    Toast.LENGTH_SHORT
                ).show()

                // Update badge
                (activity as? MainContainerActivity)?.updateCartBadge()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Not enough stock available",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateStockStatus(stock: Int) {
        view?.let { view ->
            view.findViewById<ImageButton>(R.id.increaseButton).isEnabled = quantity < stock
            view.findViewById<Button>(R.id.addToCartButton).isEnabled = stock > 0
        }
    }

    companion object {

        // Constants for all our argument keys
        private const val ARG_PLANT_ID = "plantId"
        private const val ARG_PLANT_SKU = "plantSKU"
        private const val ARG_PLANT_NAME = "plantName"
        private const val ARG_PLANT_PRICE = "plantPrice"
        private const val ARG_PLANT_DESC = "plantDesc"
        private const val ARG_PLANT_STOCK = "plantStock"
        private const val ARG_PLANT_IMAGE = "plantImage"
        private const val ARG_PLANT_UNITS_SOLD = "plantUnitsSold"
        private const val ARG_PLANT_RATING = "plantRating"
        private const val ARG_PLANT_WIDTH = "plantWidth"
        private const val ARG_PLANT_HEIGHT = "plantHeight"
        private const val ARG_PLANT_LENGTH = "plantLength"
        private const val ARG_PLANT_WEIGHT = "plantWeight"
        private const val ARG_PLANT_ENABLED = "plantEnabled"
        private const val ARG_PLANT_CATEGORY_ID = "plantCategoryId"
        private const val ARG_PLANT_CATEGORY_NAME = "plantCategoryName"

        fun newInstance(args: Bundle): ProductDetailFragment {
            return ProductDetailFragment().apply {
                arguments = args
            }
        }


    }

}