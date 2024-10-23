package com.cotiledon.mobilApp.ui.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R


private lateinit var recyclerView: RecyclerView
private lateinit var adaptador: PlantRecyclerViewAdapter
private var cartPlants = mutableListOf<Plant>()

class ShoppingCartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_cart)



        val recyclerView = findViewById<RecyclerView>(R.id.shoppingCartRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)


        adaptador = CartRecyclerViewAdapter(cartPlants)
        recyclerView.adapter = adaptador

    }



}