package com.cotiledon.mobilApp.ui.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.managers.CartStorage
import com.cotiledon.mobilApp.ui.adapters.CartRecyclerViewAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.NumberFormat
import java.util.Locale





private lateinit var recyclerView: RecyclerView
private lateinit var adaptador: CartRecyclerViewAdapter


class ShoppingCartActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adaptador: CartRecyclerViewAdapter
    private lateinit var cartStorage: CartStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_cart)

        //Inicialización de variables
        cartStorage = CartStorage(this)
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
            val subTotalValue = cartStorage.getTotalCartPrice()
            val shipmentValue = 0.0
            val discountValue = 0.0
            val totalValue = (subTotalValue + shipmentValue - discountValue)

            findViewById<TextView>(R.id.cartSubTotal).text = formatCurrency(subTotalValue)
            findViewById<TextView>(R.id.cartShipment).text = formatCurrency(shipmentValue)
            findViewById<TextView>(R.id.cartDiscount).text = formatCurrency(discountValue)
            findViewById<TextView>(R.id.cartTotal).text = formatCurrency(totalValue)
        }

        //Carga de datos
        val cartPlants = cartStorage.loadCartItems()
        adaptador = CartRecyclerViewAdapter(
            cartPlants = cartPlants,
            cartStorage = cartStorage,
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