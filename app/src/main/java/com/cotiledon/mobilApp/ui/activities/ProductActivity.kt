package com.cotiledon.mobilApp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.dataClasses.CartPlant
import com.cotiledon.mobilApp.ui.managers.CartStorage

class ProductActivity : AppCompatActivity() {
    private lateinit var cartStorage: CartStorage
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        //Define la fuente del intent desde el que se inicia la actividad. Este es generalizado
        // buscando modificarlo según el activity que inicie la vista de producto
        val source = intent.getStringExtra("source")

        //Obtener las vistas del activity_product.xml
        val imageView: ImageView = findViewById(R.id.productViewImage)
        val nameView: TextView = findViewById(R.id.productViewName)
        val descView: TextView = findViewById(R.id.productViewDesc)
        val priceView: TextView = findViewById(R.id.productViewPrice)

        //Iniciado desde el catálogo
        when (source){
            "CatalogActivity" -> {
                //Recibir los datos
                val plantName = intent.getStringExtra("plantName")
                val plantPrice = intent.getStringExtra("plantPrice")
                val plantDesc = intent.getStringExtra("plantDesc")
                val plantImage = intent.getIntExtra("plantImage", 0)
                val plantID = intent.getStringExtra("plantID")
                val plantStock = intent.getStringExtra("plantStock")

                //Boton para agregar al carrito y modificadores de cantidad
                val addToCartButton: Button = findViewById(R.id.addToCartButton)
                val increaseQuantityButton: Button = findViewById(R.id.productQuantityIncrease)
                val decreaseQuantityButton: Button = findViewById(R.id.productQuantityDecrease)
                val quantityDisplay: TextView = findViewById(R.id.productQuantityDisplay)

                //Crear el objeto de la planta en el carrito
                val cartPlant = CartPlant(
                    plantName.toString(), plantPrice.toString(),
                    plantID.toString(), plantStock.toString(), 1, plantImage
                )

                cartStorage = CartStorage(this)

                //Incrementar la cantidad de producto
                increaseQuantityButton.setOnClickListener {
                    cartPlant.plantQuantity++
                    quantityDisplay.text = cartPlant.plantQuantity.toString()
                }

                //Decrecer la cantidad de producto
                decreaseQuantityButton.setOnClickListener {
                    if (cartPlant.plantQuantity > 1) {
                        cartPlant.plantQuantity--
                        quantityDisplay.text = cartPlant.plantQuantity.toString()
                    }
                }

                //Agregar el producto al carrito
                addToCartButton.setOnClickListener {
                    cartStorage.saveProductToCart(cartPlant)
                }

                //Definir los datos que se mostrarán en la actividad
                imageView.setImageResource(plantImage)
                nameView.text = plantName
                descView.text = plantDesc
                priceView.text = plantPrice
            }
            //Iniciado desde el Home
            "HomeActivity" -> {
                //Recibir los datos
                val plantNameHome = intent.getStringExtra("plantName")
                val plantPriceHome = intent.getStringExtra("plantPrice")
                val plantImageHome = intent.getIntExtra("plantImage", 0)

                //Definir los datos que se mostrarán en la actividad
                imageView.setImageResource(plantImageHome)
                nameView.text = plantNameHome
                priceView.text = plantPriceHome
            }
        }

        //Ir a al vista de carrito
        val cart = findViewById<ImageView>(R.id.cart_product)
        cart.setOnClickListener{
            val intent4 = Intent(this, ShoppingCartActivity::class.java)
            startActivity(intent4)
        }
    }
}


