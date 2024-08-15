package com.cotiledon.mobilApp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class ProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product)
        val cart = findViewById<ImageView>(R.id.cart_product)
        cart.setOnClickListener{
            val intent4 = Intent(this,ShoppingCartActivity::class.java)
            startActivity(intent4)
    }
}
}