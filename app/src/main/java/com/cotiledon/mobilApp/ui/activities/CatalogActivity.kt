package com.cotiledon.mobilApp.ui.activities

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.adapters.PlantRecyclerViewAdapter
import com.cotiledon.mobilApp.ui.dataClasses.Plant
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CatalogActivity : AppCompatActivity() {

    private lateinit var retrofit: Retrofit
    private lateinit var plantApi: PlantApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        // Inicializar Retrofit
        retrofit = Retrofit.Builder()
            .baseUrl("http://52.15.36.189:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        plantApi = retrofit.create(PlantApi::class.java)
        loadNextPage()
    }

    private fun loadNextPage() {
        if (isLoading) return
        isLoading = true

        plantApi.getPlants(currentPage, pageSize).enqueue(object : retrofit2.Callback<List<Plant>> {
            override fun onResponse(call: Call<List<Plant>>, response: retrofit2.Response<List<Plant>>) {
                if (response.isSuccessful) {
                    response.body()?.let { plants ->
                        setUpPlants(plants)
                        currentPage++
                    }
                } else {
                    Toast.makeText(this@CatalogActivity, "Error al cargar productos: ${response.message()}", Toast.LENGTH_LONG).show()
                }
                isLoading = false
            }

            override fun onFailure(call: Call<List<Plant>>, t: Throwable) {
                Toast.makeText(this@CatalogActivity, "Error al cargar productos: ${t.message}", Toast.LENGTH_LONG).show()
                isLoading = false
            }
        })
    }

    private fun setUpPlants(plants: List<Plant>) {
        for (planta in plants) {
            val imageRes = if (Plantas.size < ImagesPlantas.size) ImagesPlantas[Plantas.size] else R.drawable.suculenta

            val instance = Plant(
                planta.plantName,
                planta.plantPrice,
                planta.plantDesc,
                planta.plantID,
                planta.plantStock,
                planta.plantCategory,
                imageRes
            )

            Plantas.add(instance)
        }

        adaptador.notifyDataSetChanged()
    }
}
