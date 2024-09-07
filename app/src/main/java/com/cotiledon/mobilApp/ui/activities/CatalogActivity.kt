package com.cotiledon.mobilApp.ui.activities

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.databinding.ActivityCatalogBinding
import com.cotiledon.mobilApp.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject
import com.google.gson.Gson
import org.json.JSONException
import java.io.File

class CatalogActivity : AppCompatActivity() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var adaptador: PlantRecyclerViewAdapter
    private var Plantas = mutableListOf<Plant>()

    private val ImagesPlantas = arrayOf(R.drawable.suculenta, R.drawable.monstera, R.drawable.lengua_de_suegra, R.drawable.manto_de_eva, R.drawable.pata_de_elefante, R.drawable.girasol,
        R.drawable.yuca, R.drawable.helecho_paragua, R.drawable.canelo, R.drawable.menta)

    private lateinit var objetosPlantas: JSONArray

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        try {
            objetosPlantas = JSONArray(resources.openRawResource(R.raw.products).bufferedReader().use { it.readText() })
        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al cargar el catálogo", Toast.LENGTH_LONG).show()
            return
        }

        val bundle = intent.extras
        val catTitle = bundle?.getString("tituloCat")
        val tituloCat= findViewById<TextView>(R.id.CatalogName)
        tituloCat.text = catTitle

        recyclerView = findViewById(R.id.catalogRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        setUpPlants()

        adaptador = PlantRecyclerViewAdapter(Plantas) { planta ->

            val intent = Intent(this, ProductActivity::class.java)
            intent.putExtra("plantName", planta.plantName)
            intent.putExtra("plantPrice", planta.plantPrice)
            intent.putExtra("plantDesc", planta.plantDesc)
            intent.putExtra("plantImage", planta.plantImage)
            startActivity(intent)
        }

        recyclerView.adapter = adaptador

    }

    private fun setUpPlants(){

        Plantas.clear()

        val maxImages = ImagesPlantas.size

        for (i in 0 until objetosPlantas.length()){
            val plantaJson = objetosPlantas.getJSONObject(i)

            val imageRes = if (i < maxImages) ImagesPlantas[i] else R.drawable.suculenta

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
    }

}
