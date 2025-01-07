package com.cotiledon.mobilApp.ui.backend.catalog

import com.cotiledon.mobilApp.ui.backend.GlobalValues
import com.cotiledon.mobilApp.ui.dataClasses.catalog.PlantFilterParams
import com.cotiledon.mobilApp.ui.dataClasses.plant.Plant
import com.cotiledon.mobilApp.ui.dataClasses.plant.PlantResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitCatalogClient(private val baseUrl: String) {

    private val catalogApiService: CatalogApiService

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .retryOnConnectionFailure(true)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        catalogApiService = retrofit.create(CatalogApiService::class.java)
    }

    suspend fun getPlants(
        page: Int,
        pageSize: Int,
        filterParams: PlantFilterParams? = null
    ): PlantResponse<Plant> {
        return catalogApiService.getPlants(
            page = page,
            pageSize = pageSize,
            environment = filterParams?.environment,
            petFriendly = filterParams?.petFriendly,
            rating = filterParams?.rating,
            maxPrice = filterParams?.maxPrice,
            minPrice = filterParams?.minPrice,
            temperatureTolerance = filterParams?.temperatureTolerance,
            lighting = filterParams?.lighting,
            irrigationType = filterParams?.irrigationType,
            orderBy = filterParams?.orderBy,
            order = filterParams?.order,
            size = filterParams?.size
        )
    }

    suspend fun searchPlants(
        page: Int,
        pageSize: Int,
        searchQuery: String
    ): PlantResponse<Plant> {
        return catalogApiService.searchPlants(
            page = page,
            pageSize = pageSize,
            searchQuery = searchQuery
        )
    }

    companion object {
        fun createCatalogClient() = RetrofitCatalogClient(GlobalValues.backEndIP)
    }
}