package com.cotiledon.mobilApp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.dataClasses.Plant
import org.json.JSONArray
import org.json.JSONException

class CatalogActivity : AppCompatActivity() {

    // Inicializamos las variables que contendrán el Recycler View, Adapter y Lista con Objetos de la clase Plant
    // recyclerView y adaptador deben ser lateinit ya que si no generarán problemas al cargar la app
    private lateinit var recyclerView: RecyclerView
    private lateinit var adaptador: PlantRecyclerViewAdapter
    private var Plantas = mutableListOf<Plant>()

    //Se crea un array con los drawables de cada objeto ya que no se tiene otra forma de agregar las imágenes por medio de un url en JSON
    private val ImagesPlantas = arrayOf(R.drawable.suculenta, R.drawable.monstera, R.drawable.lengua_de_suegra, R.drawable.manto_de_eva, R.drawable.pata_de_elefante, R.drawable.girasol,
        R.drawable.yuca, R.drawable.helecho_paragua, R.drawable.canelo, R.drawable.menta)

    //Se inicializa la variable que contendrá los objetos del array de JSON.
    private lateinit var objetosPlantas: JSONArray

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        //Se pobla la variable objetosPlantas con los objetos del Array de JSON. Se deja un try/catch para que la app no se caiga y se sepa si hay un problema en cargar la data
        try {
            objetosPlantas = JSONArray(resources.openRawResource(R.raw.products).bufferedReader().use { it.readText() })
        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al cargar el catálogo", Toast.LENGTH_LONG).show()
            return
        }

        //Se obtiene el dato del título de la categoría que se clickeó en el HomeActivity para mostrarlo en el título de la vista de catálogo
        val bundle = intent.extras
        val catTitle = bundle?.getString("tituloCat")
        val tituloCat= findViewById<TextView>(R.id.CatalogName)
        tituloCat.text = catTitle

        //Se obtiene el view para la variable del Recycler y se le asigna su LayoutManager
        recyclerView = findViewById(R.id.catalogRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //Se llama a la función setUpPlants para poblar la lista de plantas desde el JSON
        setUpPlants()

        //Se asigna el valor al adaptador definido como el adaptador creado para este recycler view que toma la lista de plantas creada
        //Para poder traspasar los datos a la vista detallada de cada producto, se definene los putExtra y se le das start al intent de la vista detallada de producto
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

        //Se vincula el adaptador del recycler view con nuestro adaptador
        recyclerView.adapter = adaptador

    }

    //Definición de la función setUpPlant que pobla el array Plantas creado anteriormente con los objetos instanciados de la clase Plant
    private fun setUpPlants(){

        //Asegurarse que la lista Plantas está vacía siempre antes de agregar objetos
        Plantas.clear()

        //Asegurarse que la lista de Imágenes sea de mismo tamaño que la lista de Plantas
        val maxImages = ImagesPlantas.size

        //Recorrer los JSONObjects del JSONArray para poder extraer la data de cada uno
        for (i in 0 until objetosPlantas.length()){
            val plantaJson = objetosPlantas.getJSONObject(i)

            //Si los arrays de imágenes y plantas son de distinto tamaño, para que no se caiga la app, se define que todas las imágenes sean la primera imágen
            val imageRes = if (i < maxImages) ImagesPlantas[i] else R.drawable.suculenta

            //Instanciar cada objeto Plant con los datos de los JSONObjects obtenidos y la imágen en la misma posición
            val instance = Plant(
                plantaJson.getString("name"),
                plantaJson.getString("price"),
                plantaJson.getString("desc"),
                plantaJson.getString("id"),
                plantaJson.getString("stock"),
                plantaJson.getString("cat"),
                imageRes
            )

            //Agregar el objeto Plant al array de plantas
            Plantas.add(instance)
        }
    }

}
