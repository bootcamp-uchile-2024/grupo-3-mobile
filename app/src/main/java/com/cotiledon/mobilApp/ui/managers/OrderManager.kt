package com.cotiledon.mobilApp.ui.managers

import com.cotiledon.mobilApp.ui.dataClasses.order.PaymentDetails
import com.cotiledon.mobilApp.ui.dataClasses.order.ShippingDetails

object OrderManager {
    var shippingDetails: ShippingDetails? = null
    var paymentDetails: PaymentDetails? = null
}