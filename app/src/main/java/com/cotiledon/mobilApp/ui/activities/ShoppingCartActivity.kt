package com.cotiledon.mobilApp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.managers.CartStorageManager
import com.cotiledon.mobilApp.ui.adapters.CartRecyclerViewAdapter
import java.text.NumberFormat
import java.util.Locale





private lateinit var recyclerView: RecyclerView
private lateinit var adaptador: CartRecyclerViewAdapter


class ShoppingCartActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adaptador: CartRecyclerViewAdapter
    private lateinit var cartStorageManager: CartStorageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_cart)

        //Inicialización de variables
        cartStorageManager = CartStorageManager(this)
        recyclerView = findViewById(R.id.shoppingCartRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //Formato de los datos de la compra
        fun formatCurrency(amount: Double): String {
            val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
            var formattedAmount = format.format(amount)
            formattedAmount = formattedAmount.replace(",", ".").replace("\u00A0", "")
            return formattedAmount
        }

        //Función para actualizar los totales
        fun updateTotals() {
            val subTotalValue = cartStorageManager.getTotalCartPrice()
            val shipmentValue = 0.0
            val discountValue = 0.0
            val totalValue = (subTotalValue + shipmentValue - discountValue)

            findViewById<TextView>(R.id.cartSubTotal).text = formatCurrency(subTotalValue)
            findViewById<TextView>(R.id.cartShipment).text = formatCurrency(shipmentValue)
            findViewById<TextView>(R.id.cartDiscount).text = formatCurrency(discountValue)
            findViewById<TextView>(R.id.cartTotal).text = formatCurrency(totalValue)
        }

        //Carga de datos
        val cartPlants = cartStorageManager.loadCartItems()
        adaptador = CartRecyclerViewAdapter(
            cartPlants = cartPlants,
            cartStorageManager = cartStorageManager,
            onItemRemoved = {
                updateTotals()
            }
        )
        recyclerView.adapter = adaptador

        //Botón para volver a la actividad anterior
        val backButton = findViewById<ImageButton>(R.id.cartBackButton)
        backButton.setOnClickListener {
            finish()
        }



        //Inicializar totales
        updateTotals()

        //Botón para finalizar la compra
        val buyButton = findViewById<Button>(R.id.cartBuyButton)
        buyButton.setOnClickListener {
            intent = Intent(this, ShippingDetailsActivity::class.java)
            startActivity(intent)
        }
    }
}