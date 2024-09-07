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

        // Recibir los datos desde el intent
        val plantName = intent.getStringExtra("plantName")
        val plantPrice = intent.getStringExtra("plantPrice")
        val plantDesc = intent.getStringExtra("plantDesc")
        val plantImage = intent.getIntExtra("plantImage", 0)

        // Obtener las vistas
        val imageView: ImageView = findViewById(R.id.productViewImage)
        val nameView: TextView = findViewById(R.id.productViewName)
        val descView: TextView = findViewById(R.id.productViewDesc)
        val priceView: TextView = findViewById(R.id.productViewPrice)

        // Establecer los datos en las vistas
        imageView.setImageResource(plantImage)
        nameView.text = plantName
        descView.text = plantDesc
        priceView.text = plantPrice

        val cart = findViewById<ImageView>(R.id.cart_product)
        cart.setOnClickListener{
            val intent4 = Intent(this, ShoppingCartActivity::class.java)
            startActivity(intent4)
    }
}
}