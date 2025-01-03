package com.cotiledon.mobilApp.ui.backend.catalog

import com.cotiledon.mobilApp.ui.dataClasses.plant.Plant
import com.cotiledon.mobilApp.ui.dataClasses.plant.PlantResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CatalogApiService {
    @GET("catalogo")
    suspend fun getPlants(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): PlantResponse<Plant>
}