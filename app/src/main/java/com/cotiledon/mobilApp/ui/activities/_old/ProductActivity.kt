package com.cotiledon.mobilApp.ui.activities._old

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.dataClasses.CartPlant
import com.cotiledon.mobilApp.ui.dataClasses.PlantProductData
import com.cotiledon.mobilApp.ui.managers.CartStorageManager
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Locale

class ProductActivity : AppCompatActivity() {
    private lateinit var cartStorageManager: CartStorageManager
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        //Obtener las vistas del activity_product.xml
        val imageView: ImageView = findViewById(R.id.productViewImage)
        val nameView: TextView = findViewById(R.id.productViewName)
        val descView: TextView = findViewById(R.id.productViewDesc)
        val priceView: TextView = findViewById(R.id.productViewPrice)

        //Define la fuente del intent desde el que se inicia la actividad. Este es generalizado
        // buscando modificarlo según el activity que inicie la vista de producto
        val source = intent?.getStringExtra("source")

        when (source) {
            "CatalogActivity" -> handleCatalogSource()
            "HomeActivity" -> handleHomeSource(imageView, nameView, priceView)
        }

        setupCartNavigation()
    }

    private fun handleCatalogSource() {

        val plantData = getPlantDataFromIntent()

        val addToCartButton: Button = findViewById(R.id.addToCartButton)
        val increaseQuantityButton: Button = findViewById(R.id.productQuantityIncrease)
        val decreaseQuantityButton: Button = findViewById(R.id.productQuantityDecrease)
        val quantityDisplay: TextView = findViewById(R.id.productQuantityDisplay)

        val cartPlant = createCartPlant(plantData)

        cartStorageManager = CartStorageManager(this)

        setupQuantityControls(
            increaseQuantityButton,
            decreaseQuantityButton,
            quantityDisplay,
            cartPlant
        )

        setupAddToCart(addToCartButton, cartPlant)

        displayPlantInfo(plantData)
    }

    private fun getPlantDataFromIntent(): PlantProductData {
        return PlantProductData(
            id = intent.getIntExtra("plantId", -1),
            name = intent.getStringExtra("plantName") ?: "",
            price = intent.getDoubleExtra("plantPrice", 0.0),
            description = intent.getStringExtra("plantDesc") ?: "",
            image = intent.getStringExtra("plantImage") ?: "",
            stock = intent.getIntExtra("plantStock", 0),

            sku = intent.getStringExtra("plantSKU") ?: "",
            categoryId = intent.getIntExtra("plantCategory", -1),
            unitsSold = intent.getIntExtra("plantUnitsSold", 0),
            rating = intent.getIntExtra("plantRating", 0),
            width = intent.getIntExtra("plantWidth", 0),
            height = intent.getIntExtra("plantHeight", 0),
            length = intent.getIntExtra("plantLength", 0),
            weight = intent.getIntExtra("plantWeight", 0),
            enabled = intent.getBooleanExtra("plantEnabled", false)
        )
    }

    private fun createCartPlant(plantData: PlantProductData): CartPlant {
        return CartPlant(
            plantName = plantData.name,
            plantPrice = plantData.price.toString(),
            plantId = plantData.id.toString(),
            plantStock = plantData.stock.toString(),
            plantQuantity = 1,
            plantImage = plantData.image
        )
    }

    @SuppressLint("SetTextI18n")
    private fun setupQuantityControls(
        increaseButton: Button,
        decreaseButton: Button,
        quantityDisplay: TextView,
        cartPlant: CartPlant
    ) {
        increaseButton.setOnClickListener {
            cartPlant.plantQuantity++
            quantityDisplay.text = cartPlant.plantQuantity.toString()
        }

        decreaseButton.setOnClickListener {
            if (cartPlant.plantQuantity > 1) {
                cartPlant.plantQuantity--
                quantityDisplay.text = cartPlant.plantQuantity.toString()
            }
        }
    }

    private fun setupAddToCart(addToCartButton: Button, cartPlant: CartPlant) {
        addToCartButton.setOnClickListener {
            cartStorageManager.saveProductToCart(cartPlant)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayPlantInfo(plantData: PlantProductData) {
        val imageView = findViewById<ImageView>(R.id.productViewImage)
        val nameView = findViewById<TextView>(R.id.productViewName)
        val descView = findViewById<TextView>(R.id.productViewDesc)
        val priceView = findViewById<TextView>(R.id.productViewPrice)

        loadProductImage(plantData.image, imageView)

        nameView.text = plantData.name
        descView.text = plantData.description

        val formatter = NumberFormat.getNumberInstance(Locale.GERMAN)

        priceView.text = "$ ${formatter.format(plantData.price)}"
    }

    private fun loadProductImage(imageUrl: String, imageView: ImageView) {
        // Check if the URL is not empty or invalid
        if (imageUrl.isBlank()) {
            // Load a placeholder if the URL is empty
            Picasso.get()
                .load(R.drawable.suculenta)
                .into(imageView)
            return
        }

        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.user_24)
            .error(R.drawable.suculenta)
            // Resize the image to fit the ImageView dimensions
            // Replace these values with your desired dimensions or
            // get them dynamically from your ImageView
            .resize(800, 800)
            // This maintains the image's aspect ratio
            .centerInside()
            // Load the image into our ImageView
            .into(imageView, object : Callback {
                override fun onSuccess() {
                    // Image loaded successfully
                    // You could fade in the image or update UI elements here
                }

                override fun onError(e: Exception) {
                    // Handle the error case
                    Toast.makeText(
                        this@ProductActivity,
                        "Error loading image: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    fun loadCartItemImage(cartPlant: CartPlant, imageView: ImageView) {
        loadProductImage(cartPlant.plantImage, imageView)
    }

    private fun handleHomeSource(
        imageView: ImageView,
        nameView: TextView,
        priceView: TextView
    ) {
        val plantNameHome = intent.getStringExtra("plantName")
        val plantPriceHome = intent.getStringExtra("plantPrice")
        val plantImageHome = intent.getIntExtra("plantImage", 0)

        imageView.setImageResource(plantImageHome)
        nameView.text = plantNameHome
        priceView.text = plantPriceHome
    }

    private fun setupCartNavigation() {
        val cart = findViewById<ImageView>(R.id.cart_product)
        cart.setOnClickListener {
            val intent4 = Intent(this, ShoppingCartActivity::class.java)
            startActivity(intent4)
        }
    }

}