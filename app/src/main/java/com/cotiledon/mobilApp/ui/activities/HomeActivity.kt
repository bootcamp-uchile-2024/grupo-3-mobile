package com.cotiledon.mobilApp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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

        //Capacidad de clicear el primer producto e ir a la vista detallada de este
        val producto1 = findViewById<CardView>(R.id.product1_carouselView)
        producto1.setOnClickListener{
            val intent3 = Intent(this,ProductActivity::class.java)
            startActivity(intent3)
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

        }
}