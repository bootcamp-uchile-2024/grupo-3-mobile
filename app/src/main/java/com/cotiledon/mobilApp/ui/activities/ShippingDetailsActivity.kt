package com.cotiledon.mobilApp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.dataClasses.ShippingDetails

class ShippingDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipping_details)

        val nameInput = findViewById<EditText>(R.id.inputName)
        val addressInput = findViewById<EditText>(R.id.inputAddress)
        val cityInput = findViewById<EditText>(R.id.inputCity)
        val regionInput = findViewById<EditText>(R.id.inputRegion)
        val zipCodeInput = findViewById<EditText>(R.id.inputZipCode)
        val phoneInput = findViewById<EditText>(R.id.inputPhone)
        val emailInput = findViewById<EditText>(R.id.inputEmail)

        val continueButton = findViewById<Button>(R.id.buttonContinue)
        continueButton.setOnClickListener {
            // Validación de los campos
            if (nameInput.text.isBlank() || addressInput.text.isBlank() ||
                cityInput.text.isBlank() || regionInput.text.isBlank() ||
                zipCodeInput.text.isBlank() || phoneInput.text.isBlank() || emailInput.text.isBlank()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
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

            val shippingDetails = ShippingDetails(
                name = nameInput.text.toString(),
                address = addressInput.text.toString(),
                city = cityInput.text.toString(),
                region = regionInput.text.toString(),
                zipCode = zipCodeInput.text.toString(),
                phone = phoneInput.text.toString(),
                email = emailInput.text.toString()
            )

            // Paso de datos a la siguiente actividad
            val intent = Intent(this, PaymentDetailsActivity::class.java)
            intent.putExtra("shippingDetails", shippingDetails)
            startActivity(intent)
        }
    }
}
