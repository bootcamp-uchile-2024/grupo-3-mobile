package com.cotiledon.mobilApp.ui.managers

import com.cotiledon.mobilApp.ui.dataClasses.order.PaymentDetails
import com.cotiledon.mobilApp.ui.dataClasses.order.ShippingDetails
import com.cotiledon.mobilApp.ui.dataClasses.order.DireccionEnvio
import com.cotiledon.mobilApp.ui.dataClasses.order.OrderRequest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object OrderManager {
    var shippingDetails: ShippingDetails? = null
    var paymentDetails: PaymentDetails? = null

    private var visitorDetails: ShippingDetails? = null

    fun saveVisitorDetails(details: ShippingDetails) {
        visitorDetails = details
    }

    fun getVisitorDetails(): ShippingDetails? = visitorDetails

    fun clearVisitorDetails() {
        visitorDetails = null
    }

    fun createOrderRequest(): OrderRequest? {
        shippingDetails?.let { details ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = Date()
            val fechaCreacion = dateFormat.format(currentDate)

            val calendar = Calendar.getInstance()
            calendar.time = currentDate
            calendar.add(Calendar.DAY_OF_MONTH, 3)
            val fechaEntrega = dateFormat.format(calendar.time)

            // Create DireccionEnvio with properly separated address components
            val direccionEnvio = DireccionEnvio(
                comuna = details.city,
                calle = details.address,        // Now contains only street name
                numero = details.streetNumber ?: "",
                departamento = details.department ?: "",
                referencia = details.reference ?: ""
            )

            val receptor = "${details.name} ${details.lastName}"

            return OrderRequest(
                fechaCreacion = fechaCreacion,
                idMedioPago = 1,
                idEstado = 1,
                idTipoDespacho = 1,
                receptor = receptor,
                fechaEntrega = fechaEntrega,
                direccionEnvio = direccionEnvio,
                idxDireccion = 0
            )
        }
        return null
    }

    fun clearOrderData() {
        shippingDetails = null
        paymentDetails = null
    }
}