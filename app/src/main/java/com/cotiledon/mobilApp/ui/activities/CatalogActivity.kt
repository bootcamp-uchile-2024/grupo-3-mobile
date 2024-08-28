package com.cotiledon.mobilApp.ui.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cotiledon.mobilApp.R
import org.json.JSONArray
import org.json.JSONObject

@Suppress("UNCHECKED_CAST")
class CatalogActivity : AppCompatActivity() {

    val Plantas: MutableList<Plant> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_catalog)



    }

    val objetosPlantas: JSONArray = JSONArray(resources.openRawResource((R.raw.products)).bufferedReader().use{it.readLine()})

    fun setUpPlants(){
        for (i in 0 ..< objetosPlantas.length()){
            val Planta: JSONObject = JSONObject(objetosPlantas.get(i).toString())
            val instance = Plant(Planta.get("name").toString(),Planta.get("price").toString(),Planta.get("desc").toString(),Planta.get("id") as Int,Planta.get("stock") as Int,
                Planta.get("cat").toString(),Planta.get("pics") as Array<String>)
            Plantas.add(instance)
        }
    }

}
