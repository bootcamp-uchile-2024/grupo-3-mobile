package com.cotiledon.mobilApp.ui.backend.ia

import com.cotiledon.mobilApp.ui.dataClasses.ia.IABase64Request
import com.cotiledon.mobilApp.ui.dataClasses.ia.IAResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface IAApiService {
    @POST("/iaconsultas/base")
    suspend fun sendImageBase64(@Body request: IABase64Request): IAResponse
}