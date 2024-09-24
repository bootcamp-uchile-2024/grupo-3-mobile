package com.cotiledon.mobilApp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.cotiledon.mobilApp.R

class ProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        //Define la fuente del intent desde el que se inicia la actividad
        val source = intent.getStringExtra("source")

        //Obtener las vistas del activity_product.xml
        val imageView: ImageView = findViewById(R.id.productViewImage)
        val nameView: TextView = findViewById(R.id.productViewName)
        val descView: TextView = findViewById(R.id.productViewDesc)
        val priceView: TextView = findViewById(R.id.productViewPrice)

        when (source){
            "CatalogActivity" -> {
                //Recibir los datos desde el intent de CatalogActivity
                val plantName = intent.getStringExtra("plantName")
                val plantPrice = intent.getStringExtra("plantPrice")
                val plantDesc = intent.getStringExtra("plantDesc")
                val plantImage = intent.getIntExtra("plantImage", 0)

                //Definir los datos que se mostrarán en la actividad a partir de lo importado desde el intent de CatalogActivity
                imageView.setImageResource(plantImage)
                nameView.text = plantName
                descView.text = plantDesc
                priceView.text = plantPrice

            }
            "HomeActivity" -> {
                //Recibir los datos desde el intent de HomeActivity
                val plantNameHome = intent.getStringExtra("plantName")
                val plantPriceHome = intent.getStringExtra("plantPrice")
                val plantImageHome = intent.getIntExtra("plantImage", 0)

                //Definir los datos que se mostrarán en la actividad a partir de lo importado desde el intent de HomeActivity
                imageView.setImageResource(plantImageHome)
                nameView.text = plantNameHome
                priceView.text = plantPriceHome
            }
        }


        //Permitir ir a la vista de carrito al clickear en el símbolo del carrito de compras
        val cart = findViewById<ImageView>(R.id.cart_product)
        cart.setOnClickListener{
            val intent4 = Intent(this, ShoppingCartActivity::class.java)
            startActivity(intent4)
    }
}
}