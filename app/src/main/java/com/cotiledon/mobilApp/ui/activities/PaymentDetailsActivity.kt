package com.cotiledon.mobilApp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.dataClasses.PaymentDetails
import com.cotiledon.mobilApp.ui.managers.OrderManager
import java.io.Serializable

class PaymentDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_details)

        // Obtiene los detalles de envío de la actividad anterior
        val shippingDetails = intent.getStringExtra("shippingDetails")

        val paymentMethodInput = findViewById<RadioButton>(R.id.creditCardOption)
        val cardNumberInput = findViewById<EditText>(R.id.inputCardNumber)
        val cardHolderNameInput = findViewById<EditText>(R.id.inputCardHolderName)
        val expiryDateInput = findViewById<EditText>(R.id.inputExpiryDate)
        val securityCodeInput = findViewById<EditText>(R.id.inputSecurityCode)

        fun savePaymentDetails() {
            // Guarda los detalles de pago en OrderManager
                OrderManager.paymentDetails = PaymentDetails(
                    paymentMethod = paymentMethodInput.text.toString(),
                    cardNumber = cardNumberInput.text.toString(),
                    carHolderName = cardHolderNameInput.text.toString(),
                    expiryDate = expiryDateInput.text.toString(),
                    securityCode = securityCodeInput.text.toString()
                )
            }

        fun continueToPaymentConfirmation() {
            // Paso de datos a ConfirmationActivity
            val intent = Intent(this, PaymentConfirmationActivity::class.java)
            startActivity(intent)
        }

        val continueButton = findViewById<Button>(R.id.buttonContinue)
        continueButton.setOnClickListener {
            // Validación de campos vacíos
            if (paymentMethodInput.text.isBlank() || cardNumberInput.text.isBlank() ||
                cardHolderNameInput.text.isBlank() || expiryDateInput.text.isBlank() ||
                securityCodeInput.text.isBlank()
            ) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Validación del número de tarjeta (debería tener 16 dígitos)
            if (cardNumberInput.text.length != 16) {
                cardNumberInput.error = "El número de tarjeta debe tener 16 dígitos"
                return@setOnClickListener
            }

            // Validación del código de seguridad (debería tener 3 o 4 dígitos)
            if (securityCodeInput.text.length !in 3..4) {
                securityCodeInput.error = "El código de seguridad debe tener 3 o 4 dígitos"
                return@setOnClickListener
            }

            // Validación de la fecha de expiración (mes y año en formato MM/AA)
            val expiryDateRegex = Regex("""^(0[1-9]|1[0-2])/[0-9]{2}$""")
            if (!expiryDateRegex.matches(expiryDateInput.text.toString())) {
                expiryDateInput.error = "Fecha de expiración no válida (MM/AA)"
                return@setOnClickListener
            }

            savePaymentDetails()
            continueToPaymentConfirmation()
            finish()

        }
    }
}
