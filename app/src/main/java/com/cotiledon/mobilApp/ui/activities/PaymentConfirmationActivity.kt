package com.cotiledon.mobilApp.ui.activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.dataClasses.CartPlant
import com.cotiledon.mobilApp.ui.dataClasses.PaymentDetails
import com.cotiledon.mobilApp.ui.dataClasses.ShippingDetails
import com.cotiledon.mobilApp.ui.managers.CartStorage
import com.cotiledon.mobilApp.ui.managers.OrderManager

class PaymentConfirmationActivity : AppCompatActivity() {
    private lateinit var productsTableLayout: TableLayout
    private lateinit var cartStorage: CartStorage

    //Constantes
    companion object {
        const val IVA_RATE = 0.19 // 19% IVA
        const val SHIPPING_TIME = "1 día"
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_confirmation)

        cartStorage = CartStorage(this)

        productsTableLayout = findViewById(R.id.productsTableLayout)

        val shippingDetails = OrderManager.shippingDetails
        val paymentDetails = OrderManager.paymentDetails



        //Obtener datos de carrito desde CartStorage
        val cartItems = cartStorage.loadCartItems()

        //Mostrar los datos en forma dinámica
        if (paymentDetails != null) {
            if (shippingDetails != null) {
                displayOrderConfirmation(paymentDetails,shippingDetails, cartItems)
            }
        }

        //Limpiar el carrito después de comprar
        cartStorage.clearCart()
    }

    @SuppressLint("DefaultLocale")
    private fun displayOrderConfirmation(
        paymentDetails: PaymentDetails,
        shippingDetails: ShippingDetails,
        cartItems: List<CartPlant>) {
        //Núemero de pedido (en un futuro será controlado por el backend)
        findViewById<TextView>(R.id.orderNumber).text = "#1"

        //Limpiar las filas existentes en la tabla menos los titulos
        while (productsTableLayout.childCount > 1) {
            productsTableLayout.removeViewAt(1)
        }

        //Sumar valores de las plantas
        val total = cartStorage.getTotalCartPrice()

        //Calcular IVA
        val iva = total * IVA_RATE

        //Subtotal
        val subtotal = total - iva

        //Agregar filas de resumen
        addSummaryRow("Subtotal: $", "${String.format("%.2f", subtotal)} (IVA no incluido)")
        //Costo de envío a ser modificado desde el backend
        addSummaryRow("Envío: $", "0" + "(envío a domicilio en ${SHIPPING_TIME})")
        addSummaryRow("IVA: $", String.format("%.2f", iva))
        addSummaryRow("Método de pago:", paymentDetails.paymentMethod)
        addTotalRow("Total: $", String.format("%.2f", total))

        // Update customer information
        updateCustomerInformation(paymentDetails, shippingDetails)
    }

    private fun addProductRow(plant: CartPlant) {
        val row = createTableRow()

        row.addView(createTextView(plant.plantName))
        row.addView(createTextView(plant.plantQuantity.toString()))
        row.addView(createTextView("$ ${String.format("%.2f", plant.plantPrice)}"))

        productsTableLayout.addView(row)
    }

    private fun updateCustomerInformation(
        paymentDetails: PaymentDetails,
        shippingDetails: ShippingDetails
    ) {
        //Actualizar datos del comprador
        findViewById<TextView>(R.id.customerEmail).text = "Correo electrónico: ${shippingDetails.email}"
        findViewById<TextView>(R.id.customerPhone).text = "Tel: ${shippingDetails.phone}"

        //Actualizar dirección de facturación
        findViewById<TextView>(R.id.billingName).text = shippingDetails.name
        findViewById<TextView>(R.id.billingZipCode).text = shippingDetails.zipCode
        findViewById<TextView>(R.id.billingAddress).text = "${shippingDetails.address} + ${shippingDetails.city} + ${shippingDetails.region}"

        //Actualizar dirección de envío
        findViewById<TextView>(R.id.shippingName).text = shippingDetails.name
        findViewById<TextView>(R.id.shippingZipCode).text = shippingDetails.zipCode
        findViewById<TextView>(R.id.shippingAddress).text = shippingDetails.address
    }

    private fun addSummaryRow(label: String, value: String) {
        val row = createTableRow()

        row.addView(createTextView(label, true))
        row.addView(createTextView("")) // Empty middle column
        row.addView(createTextView(value))

        productsTableLayout.addView(row)
    }

    private fun addTotalRow(label: String, value: String) {
        val row = createTableRow().apply {
            setBackgroundColor(Color.parseColor("#F5F5F5"))
        }

        row.addView(createTextView(label, true))
        row.addView(createTextView("")) // Empty middle column
        row.addView(createTextView(value, true))

        productsTableLayout.addView(row)
    }

    private fun createTableRow(): TableRow {
        return TableRow(this).apply {
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))
        }
    }

    private fun createTextView(text: String, bold: Boolean = false): TextView {
        return TextView(this).apply {
            this.text = text
            if (bold) {
                setTypeface(null, Typeface.BOLD)
            }
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}