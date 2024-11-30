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

class CatalogActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adaptador: PlantRecyclerViewAdapter
    private var Plantas = mutableListOf<Plant>()

    private val ImagesPlantas = arrayOf(
        R.drawable.suculenta,
        R.drawable.monstera,
        R.drawable.lengua_de_suegra,
        R.drawable.manto_de_eva,
        R.drawable.pata_de_elefante,
        R.drawable.girasol,
        R.drawable.yuca,
        R.drawable.helecho_paragua,
        R.drawable.canelo,
        R.drawable.menta
    )

    // Variables para el paginado
    private var currentPage = 1
    private val pageSize = 15
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        // Obtener el título de la categoría desde el intent
        val bundle = intent.extras
        val catTitle = bundle?.getString("tituloCat")
        val tituloCat = findViewById<TextView>(R.id.CatalogName)
        tituloCat.text = catTitle

        // Configurar el RecyclerView
        recyclerView = findViewById(R.id.catalogRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adaptador = PlantRecyclerViewAdapter(Plantas) { planta ->
            val intent = Intent(this, ProductActivity::class.java)
            intent.putExtra("source", "CatalogActivity")
            intent.putExtra("plantName", planta.plantName)
            intent.putExtra("plantPrice", planta.plantPrice)
            intent.putExtra("plantDesc", planta.plantDesc)
            intent.putExtra("plantId", planta.plantID)
            intent.putExtra("plantStock", planta.plantStock)
            intent.putExtra("plantImage", planta.plantImage)
            startActivity(intent)
        }
        recyclerView.adapter = adaptador

        // Agregar scroll infinito
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                    loadNextPage()
                }
            }
        })

        // Cargar la primera página de datos
        loadNextPage()
    }

    private fun loadNextPage() {
        if (isLoading) return
        isLoading = true

        fetchProducts(currentPage, pageSize, onSuccess = { jsonArray ->
            setUpPlants(jsonArray)
            currentPage++
            isLoading = false
        }, onError = { e ->
            Toast.makeText(this, "Error al cargar productos: ${e.message}", Toast.LENGTH_LONG).show()
            isLoading = false
        })
    }

    private fun setUpPlants(jsonArray: JSONArray) {
        val maxImages = ImagesPlantas.size

        for (i in 0 until jsonArray.length()) {
            val plantaJson = jsonArray.getJSONObject(i)
            val imageRes = if (Plantas.size + i < maxImages) ImagesPlantas[Plantas.size + i] else R.drawable.suculenta

            val instance = Plant(
                plantaJson.getString("name"),
                plantaJson.getString("price"),
                plantaJson.getString("desc"),
                plantaJson.getString("id"),
                plantaJson.getString("stock"),
                plantaJson.getString("cat"),
                imageRes
            )

            Plantas.add(instance)
        }

        adaptador.notifyDataSetChanged()
    }

    private fun fetchProducts(page: Int, size: Int, onSuccess: (JSONArray) -> Unit, onError: (Exception) -> Unit) {
        AsyncTask.execute {
            try {
                val url = URL("http://52.15.36.189:8080/catalogo?page=$page&pageSize=$size\n")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().readText()
                    val jsonArray = JSONArray(response)
                    runOnUiThread {
                        onSuccess(jsonArray)
                    }
                } else {
                    runOnUiThread {
                        onError(Exception("Error en la conexión: ${connection.responseCode}"))
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    onError(e)
                }
            }
        }
    }
}
