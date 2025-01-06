package com.cotiledon.mobilApp.ui.helpers

import com.cotiledon.mobilApp.ui.dataClasses.order.OrderRequest
import com.google.gson.*
import java.lang.reflect.Type

class OrderRequestSerializer : JsonSerializer<OrderRequest> {
    override fun serialize(
        src: OrderRequest,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val jsonObject = JsonObject()

        // Add all the regular fields
        jsonObject.addProperty("fechaCreacion", src.fechaCreacion)
        jsonObject.addProperty("idMedioPago", src.idMedioPago)
        jsonObject.addProperty("idEstado", src.idEstado)
        jsonObject.addProperty("idTipoDespacho", src.idTipoDespacho)
        jsonObject.addProperty("receptor", src.receptor)
        jsonObject.addProperty("fechaEntrega", src.fechaEntrega)

        // Add the direccionEnvio object
        val direccionEnvio = JsonObject()
        direccionEnvio.addProperty("comuna", src.direccionEnvio.comuna)
        direccionEnvio.addProperty("calle", src.direccionEnvio.calle)
        direccionEnvio.addProperty("numero", src.direccionEnvio.numero)
        direccionEnvio.addProperty("departamento", src.direccionEnvio.departamento)
        direccionEnvio.addProperty("referencia", src.direccionEnvio.referencia)
        jsonObject.add("direccionEnvio", direccionEnvio)

        // Add the idxDireccion with the exact format needed
        jsonObject.addProperty("idxDireccion", src.idxDireccion)

        return jsonObject
    }
}