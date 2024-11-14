package com.cotiledon.mobilApp.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.managers.CartStorage
import com.cotiledon.mobilApp.ui.adapters.CartRecyclerViewAdapter
import java.text.NumberFormat
import java.util.Locale


private lateinit var recyclerView: RecyclerView
private lateinit var adaptador: CartRecyclerViewAdapter


class ShoppingCartActivity : AppCompatActivity() {
    private lateinit var cartStorage: CartStorage
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_cart)

        //Inicialización de variables
        cartStorage = CartStorage(this)
        recyclerView = findViewById(R.id.shoppingCartRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //Carga de datos
        val cartPlants = cartStorage.loadCartItems()
        adaptador = CartRecyclerViewAdapter(cartPlants)
        recyclerView.adapter = adaptador

        //Botón para volver a la actividad anterior
        val backButton = findViewById<ImageButton>(R.id.cartBackButton)
        backButton.setOnClickListener {
            finish()
        }

        //Datos de la compra
        val subTotal = findViewById<TextView>(R.id.cartSubTotal)
        val shipment = findViewById<TextView>(R.id.cartShipment)
        val discount = findViewById<TextView>(R.id.cartDiscount)
        val total = findViewById<TextView>(R.id.cartTotal)

        //Cálculo de los datos de la compra
        val subTotalValue = cartStorage.getTotalCartPrice()
        val shipmentValue = 0.0
        val discountValue = 0.0
        val totalValue = (subTotalValue + shipmentValue - discountValue)

        //Formato de los datos de la compra
        fun formatCurrency(amount: Double): String {
            val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
            // Usamos Chile (es_CL) como ejemplo
            var formattedAmount = format.format(amount)

            // Reemplazar la coma con un punto para los miles y eliminar cualquier espacio
            formattedAmount = formattedAmount.replace(",", ".").
            replace("\u00A0", "")
            return formattedAmount
        }

        //Mostrar los datos de la compra
        subTotal.text = formatCurrency(subTotalValue)
        shipment.text = formatCurrency(shipmentValue)
        discount.text = formatCurrency(discountValue)
        total.text = formatCurrency(totalValue)


        //Botón para finalizar la compra
        val buyButton = findViewById<Button>(R.id.cartBuyButton)
        buyButton.setOnClickListener {
            cartStorage.clearCart()
            intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}