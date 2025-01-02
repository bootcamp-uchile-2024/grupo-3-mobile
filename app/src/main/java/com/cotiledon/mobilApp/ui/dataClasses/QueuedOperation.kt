package com.cotiledon.mobilApp.ui.dataClasses

import com.cotiledon.mobilApp.ui.enums.CartOperation

data class QueuedOperation(
    val operationType: CartOperation,
    val plantId: String,
    val cartPlant: CartPlant? = null,
    val quantity: Int? = null,
    val timestamp: Long = System.currentTimeMillis()
)