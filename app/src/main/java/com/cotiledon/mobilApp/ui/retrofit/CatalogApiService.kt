package com.cotiledon.mobilApp.ui.retrofit

import com.cotiledon.mobilApp.ui.dataClasses.Plant
import com.cotiledon.mobilApp.ui.dataClasses.PlantResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CatalogApiService {
    @GET("catalogo")
    suspend fun getPlants(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): PlantResponse<Plant>
}