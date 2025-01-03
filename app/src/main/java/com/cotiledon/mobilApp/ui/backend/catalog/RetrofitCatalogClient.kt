package com.cotiledon.mobilApp.ui.backend.catalog

import com.cotiledon.mobilApp.ui.backend.GlobalValues
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
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        catalogApiService = retrofit.create(CatalogApiService::class.java)
    }

    suspend fun getPlants(page: Int, pageSize: Int): PlantResponse<Plant> =
        catalogApiService.getPlants(page, pageSize)

    companion object {
        fun createCatalogClient() = RetrofitCatalogClient(GlobalValues.backEndIP)
    }
}