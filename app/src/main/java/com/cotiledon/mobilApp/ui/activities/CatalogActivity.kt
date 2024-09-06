package com.cotiledon.mobilApp.ui.activities

import android.content.res.Resources
import android.os.Bundle
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
import java.io.File

class CatalogActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adaptador: PlantRecyclerViewAdapter
    private var Plantas = mutableListOf<Plant>()

    val ImagesPlantas = arrayOf(R.drawable.suculenta, R.drawable.monstera, R.drawable.lengua_de_suegra, R.drawable.manto_de_eva, R.drawable.pata_de_elefante, R.drawable.girasol,
        R.drawable.yuca, R.drawable.helecho_paragua, R.drawable.canelo, R.drawable.menta)

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        recyclerView = findViewById(R.id.catalogRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        setUpPlants()

        adaptador = PlantRecyclerViewAdapter(Plantas)

        recyclerView.adapter = adaptador
        recyclerView.layoutManager = LinearLayoutManager(this)

    }

    private val objetosPlantas: JSONArray = JSONArray(resources.openRawResource((R.raw.products)).bufferedReader().use{it.readLine()})

    private fun setUpPlants(){
        for (i in 0 ..< objetosPlantas.length()){
            val Planta = JSONObject(objetosPlantas.getJSONObject(i).toString())
            val instance = Plant(Planta.get("name").toString(),Planta.get("price").toString(),Planta.get("desc").toString(),
                Planta.get("id").toString(), Planta.get("stock").toString(), Planta.get("cat").toString(),ImagesPlantas[i])
            Plantas.add(instance)
        }
    }

}
