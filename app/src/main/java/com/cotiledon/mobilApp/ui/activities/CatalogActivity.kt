package com.cotiledon.mobilApp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.adapters.PlantRecyclerViewAdapter
import com.cotiledon.mobilApp.ui.dataClasses.Plant
import com.cotiledon.mobilApp.ui.retrofit.RetrofitCatalogClient
import kotlinx.coroutines.launch

import com.cotiledon.mobilApp.ui.dataClasses.PlantResponse

class CatalogActivity : AppCompatActivity() {

    // Inicializamos las variables que contendrán el Recycler View, Adapter y Lista con Objetos de la clase Plant
    // recyclerView y adaptador deben ser lateinit ya que si no generarán problemas al cargar la app
    private lateinit var recyclerView: RecyclerView
    private lateinit var adaptador: PlantRecyclerViewAdapter
    private var plantas = mutableListOf<Plant>()
    private var currentPage = 1
    private val pageSize = 10
    private var isLoading = false
    private var hasMoreItems = true

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)


        //Se obtiene el dato del título de la categoría que se clickeó en el HomeActivity para mostrarlo en el título de la vista de catálogo
        val bundle = intent.extras
        val catTitle = bundle?.getString("tituloCat")
        val tituloCat= findViewById<TextView>(R.id.CatalogName)
        tituloCat.text = catTitle

        //Se obtiene el view para la variable del Recycler y se le asigna su LayoutManager
        recyclerView = findViewById(R.id.catalogRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //Se asigna el valor al adaptador definido como el adaptador creado para este recycler view que toma la lista de plantas creada
        //Para poder traspasar los datos a la vista detallada de cada producto, se definene los putExtra y se le das start al intent de la vista detallada de producto
        adaptador = PlantRecyclerViewAdapter(plantas) { planta ->

            val intent = Intent(this, ProductActivity::class.java)
            intent.putExtra("source", "CatalogActivity")
            intent.putExtra("plantId", planta.id)
            intent.putExtra("plantName", planta.nombre)
            intent.putExtra("plantPrice", planta.precio)
            intent.putExtra("plantDesc", planta.descripcion)
            intent.putExtra("plantStock", planta.cantidad)
            intent.putExtra("plantImage", planta.imagen)
            startActivity(intent)
        }

        //Se vincula el adaptador del recycler view con nuestro adaptador
        recyclerView.adapter = adaptador

        //Se llama a la función setUpPlants para poblar la lista de plantas desde el JSON
        setUpPlants()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

            if ((!isLoading && hasMoreItems &&
                (firstVisibleItemPosition + visibleItemCount) >= totalItemCount) &&
                    firstVisibleItemPosition >= 0)
                setUpPlants()
            }
        })

        val backbttn = findViewById<ImageButton>(R.id.CatalogBackBtn)
        backbttn.setOnClickListener {
            finish()
        }

    }

    private fun setUpPlants() {
        if (isLoading) return
        isLoading = true
        adaptador.showLoading()

        lifecycleScope.launch {
            try {
                val retrofitClient = RetrofitCatalogClient.createCatalogClient()
                val response: PlantResponse<Plant> = retrofitClient.getPlants(currentPage, pageSize)

                if (response.data.isEmpty() || currentPage * pageSize >= response.totalItems) {
                    hasMoreItems = false
                } else {
                    plantas.addAll(response.data)
                    adaptador.notifyItemRangeChanged(
                        plantas.size - response.data.size,
                        response.data.size
                    )
                }
                currentPage++
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            } finally {
                isLoading = false
                adaptador.hideLoading()
            }
        }
    }


}
