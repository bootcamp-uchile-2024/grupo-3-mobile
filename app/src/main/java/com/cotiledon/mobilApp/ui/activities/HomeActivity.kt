package com.cotiledon.mobilApp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.cotiledon.mobilApp.R

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val productosCarrusel1 = arrayOf(R.id.product1_carouselView,R.id.product2_carouselView,R.id.product3_carouselView,R.id.product4_carouselView,R.id.product5_carouselView,
            R.id.product6_carouselView)
        val imagenesCarrusel1 = arrayOf(R.drawable.suculenta,R.drawable.manto_de_eva,R.drawable.girasol,R.drawable.lengua_de_suegra,R.drawable.monstera,
            R.drawable.pata_de_elefante)
        val nombresCarrusel1 = arrayOf(R.id.product1_name,R.id.product2_name,R.id.product3_name,R.id.product4_name,R.id.product5_name,R.id.product6_name)
        val preciosCarrusel1 = arrayOf(R.id.product1_price,R.id.product2_price,R.id.product3_price,R.id.product4_price,R.id.product5_price,R.id.product6_price)

        //Capacidad de clickear cualquier producto del catálogo recomendado a la vista de producto
        for (product in productosCarrusel1.indices){
            val producto = findViewById<CardView>(productosCarrusel1[product])

            val imagenDrawableId = imagenesCarrusel1[product]

            val nombreTextView = findViewById<TextView>(nombresCarrusel1[product])
            val nombre = nombreTextView.text.toString()

            val precioTextView = findViewById<TextView>(preciosCarrusel1[product])
            val precio = precioTextView.text.toString()

            producto.setOnClickListener{
                val intentproduct = Intent(this,ProductActivity::class.java)
                intentproduct.putExtra("source", "HomeActivity")
                intentproduct.putExtra("plantName",nombre)
                intentproduct.putExtra("plantPrice", precio)
                intentproduct.putExtra("plantImage",imagenDrawableId)
                startActivity(intentproduct)
            }
        }

        //Título de la vista de carrusel para pasarlo al activity de Catalog
        val tituloCarousel = findViewById<TextView>(R.id.carouselView_title)

        //Definir paso a vista de catálogo con boton que acompaña a la vista de carrusel, pasándole el título de ese carrusel
        val irCatalogo = findViewById<Button>(R.id.goToCatlg)
        irCatalogo.setOnClickListener{
            val intentcat = Intent(this,CatalogActivity::class.java)
            intentcat.putExtra("tituloCat",tituloCarousel.text.toString())
            startActivity(intentcat)
        }

        //Ir a al vista de carrito
        val cart = findViewById<ImageView>(R.id.cart_product)
        cart.setOnClickListener{
            val intent = Intent(this, ShoppingCartActivity::class.java)
            startActivity(intent)
        }

        }

    //Al cerrar la actividad, vaciar el carrito
    override fun onDestroy() {
        super.onDestroy()
        cartStorage.clearCart()
    }

}