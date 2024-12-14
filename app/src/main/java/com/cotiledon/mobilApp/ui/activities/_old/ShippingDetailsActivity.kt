package com.cotiledon.mobilApp.ui.activities._old

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.dataClasses.ShippingDetails
import com.cotiledon.mobilApp.ui.managers.OrderManager

class ShippingDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipping_details)

        val nameInput = findViewById<EditText>(R.id.inputName)
        val addressInput = findViewById<EditText>(R.id.inputAddress)
        val cityInput = findViewById<EditText>(R.id.inputCity)
        val regionInput = findViewById<EditText>(R.id.inputRegion)
        val zipCodeInput = findViewById<EditText>(R.id.inputZipCode)
        val phoneInput = findViewById<EditText>(R.id.inputPhone)
        val emailInput = findViewById<EditText>(R.id.inputEmail)

        //Guardar detalles de envío en OrderManager
        fun saveShippingDetails() {
            OrderManager.shippingDetails = ShippingDetails(
                name = nameInput.text.toString(),
                address = addressInput.text.toString(),
                city = cityInput.text.toString(),
                region = regionInput.text.toString(),
                zipCode = zipCodeInput.text.toString(),
                phone = phoneInput.text.toString(),
                email = emailInput.text.toString()
            )
        }

        //Paso a la siguiente actividad
        fun continueToPaymentDetails() {
            val intent = Intent(this, PaymentDetailsActivity::class.java)
            startActivity(intent)
        }

        val continueButton = findViewById<Button>(R.id.buttonContinue)
        continueButton.setOnClickListener {
            // Validación de los campos
            if (nameInput.text.isBlank() || addressInput.text.isBlank() ||
                cityInput.text.isBlank() || regionInput.text.isBlank() ||
                zipCodeInput.text.isBlank() || phoneInput.text.isBlank() || emailInput.text.isBlank()
            ) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Validación de formato de email
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput.text).matches()) {
                emailInput.error = "Correo electrónico no válido"
                return@setOnClickListener
            }

            // Validación de formato de teléfono
            if (!android.util.Patterns.PHONE.matcher(phoneInput.text).matches()) {
                phoneInput.error = "Número de teléfono no válido"
                return@setOnClickListener
            }
            saveShippingDetails()
            continueToPaymentDetails()
        }

    }
}