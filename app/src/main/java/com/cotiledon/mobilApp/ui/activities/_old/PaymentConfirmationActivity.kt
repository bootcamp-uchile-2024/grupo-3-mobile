package com.cotiledon.mobilApp.ui.activities._old

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.dataClasses.CartPlant
import com.cotiledon.mobilApp.ui.dataClasses.PaymentDetails
import com.cotiledon.mobilApp.ui.dataClasses.ShippingDetails
import com.cotiledon.mobilApp.ui.managers.CartStorageManager
import com.cotiledon.mobilApp.ui.managers.OrderManager

class PaymentConfirmationActivity : AppCompatActivity() {

    private lateinit var productsTableLayout: TableLayout
    private lateinit var cartStorageManager: CartStorageManager

    //Constantes
    companion object {
        const val IVA_RATE = 0.19 // 19% IVA
        const val SHIPPING_TIME = "1 día"
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_confirmation)

        cartStorageManager = CartStorageManager(this)

        productsTableLayout = findViewById(R.id.productsTableLayout)

        val shippingDetails = OrderManager.shippingDetails
        val paymentDetails = OrderManager.paymentDetails



        //Obtener datos de carrito desde CartStorageManager
        val cartItems = cartStorageManager.loadCartItems()

        //Mostrar los datos en forma dinámica
        if (paymentDetails != null) {
            if (shippingDetails != null) {
                displayOrderConfirmation(paymentDetails,shippingDetails, cartItems)
            }
        }

        //Limpiar el carrito después de comprar
        cartStorageManager.clearCart()
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
        val total = cartStorageManager.getTotalCartPrice()

        //Agregar filas de productos
        cartItems.forEach { plant ->
            addProductRow(plant)
        }

        //Calcular IVA
        val iva = total * IVA_RATE

        //Subtotal
        val subtotal = total - iva

        //Agregar filas de resumen
        addSummaryRow("Subtotal: ", formatPrice(subtotal))
        //Costo de envío a ser modificado desde el backend
        addSummaryRow("Envío: ", formatPrice(0.0))
        addSummaryRow("IVA: ", formatPrice(iva))
        addSummaryRow("Método de pago:", paymentDetails.paymentMethod)
        addTotalRow("Total: ", formatPrice(total))

        // Update customer information
        updateCustomerInformation(paymentDetails, shippingDetails)
    }

    private fun addProductRow(plant: CartPlant) {
        val row = createTableRow()

        row.addView(createTextView(plant.plantName))
        row.addView(createCenteredTextView(plant.plantQuantity.toString()))
        row.addView(createMultiLineTextView(plant.plantPrice))

        productsTableLayout.addView(row)
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

    private fun createCenteredTextView(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            gravity = Gravity.CENTER
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun createMultiLineTextView(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            maxLines = 2
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            TextUtils.TruncateAt.END
            setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))
        }
    }

    @SuppressLint("DefaultLocale")
    private fun formatPrice(price: Double): String {
        return "$ ${String.format("%.0f", price).replace(",", ".")}"
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
        findViewById<TextView>(R.id.billingAddress).text = "${shippingDetails.address}, ${shippingDetails.city}, ${shippingDetails.region}"

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

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}