package com.cotiledon.mobilApp.ui.backend.catalog

import com.cotiledon.mobilApp.ui.dataClasses.plant.Plant
import com.cotiledon.mobilApp.ui.dataClasses.plant.PlantResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CatalogApiService {
    @GET("catalogo")
    suspend fun getPlants(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("idEntorno") environment: Int? = null,
        @Query("petFriendly") petFriendly: Boolean? = null,
        @Query("puntuacion") rating: Int? = null,
        @Query("maxPrecio") maxPrice: Int? = null,
        @Query("minPrecio") minPrice: Int? = null,
        @Query("idToleranciaTemperatura") temperatureTolerance: Int? = null,
        @Query("idIluminacion") lighting: Int? = null,
        @Query("idTipoRiego") irrigationType: Int? = null,
        @Query("ordenarPor") orderBy: String? = null,
        @Query("orden") order: String? = null,
        @Query("sizePlant") size: String? = null
    ): PlantResponse<Plant>

    @GET("catalogo/search")
    suspend fun searchPlants(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("search") searchQuery: String
    ): PlantResponse<Plant>
}