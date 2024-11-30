package com.cotiledon.mobilApp.ui.retrofit

import com.cotiledon.mobilApp.ui.dataClasses.Plant
import retrofit2.http.GET
import retrofit2.http.Query

interface CatalogApiService {
    @GET("catalogo")
    suspend fun getPlants(@Query("page") page: Int, @Query("limit") limit: Int): List<Plant>
}