package com.cotiledon.mobilApp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.adapters.SubCategoryAdapter
import com.cotiledon.mobilApp.ui.dataClasses.Category

class SubCategoriesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var backButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sub_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        recyclerView = view.findViewById(R.id.rv_subcategories)
        backButton = view.findViewById(R.id.btn_back)

        //Obtiene el nombre de la categoría desde el Bundle anterior con null check
        val categoryName = arguments?.getString("categoryName") ?:""
        val categoryId = arguments?.getInt("categoryId") ?: 0

        val subCategoryTitle = view.findViewById<TextView>(R.id.subcategory_title)
        subCategoryTitle.text = categoryName


        recyclerView.layoutManager = GridLayoutManager(context, 2)

        //Carga de tarjetas de subcategorías (estáticas por ahora)
        //TODO: Implementar carga dinámica de subcategorías dependiendo de la categoría en la que se clickea
        val subcategories = listOf(
            Category("Plantas De Interior", R.drawable.indoor_plants, 1),
            Category("Plantas De Exterior", R.drawable.outdoor_plants, 2),
            Category("Pet Friendly", R.drawable.pet_friendly,3),
            Category("Productos Populares", R.drawable.popular_products,4),
            Category("Plantas Bajo Mantenimiento", R.drawable.low_maintenance,5)
        )

        val adapter = SubCategoryAdapter(subcategories) { subcategory ->
            //Función para navegar al catalogo con un click en una subcategoría
            navigateToCatalog(subcategory)
        }
        recyclerView.adapter = adapter


        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun navigateToCatalog(subcategory: Category) {
        val catalogFragment = CatalogFragment.newInstance()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, catalogFragment)
            .addToBackStack(null)
            .commit()
    }

    //Companion object para poder generar la nueva instancia del fragment desde un fragment anterior y entrega el Bundle con datos (fragment factory)
    companion object {
        //Función para crear una nueva instancia del fragment desde el fragment anterior
        fun newInstance(args: Bundle): SubCategoriesFragment {
            return SubCategoriesFragment().apply {
                arguments = args
                }
        }

        //Función para crear el Bundle con los argumentos desde el fragment anterior
        fun createArguments(categoryName: String, categoryId: Int): Bundle {
            return Bundle().apply {
                putString("categoryName", categoryName)
                putInt("categoryId", categoryId)
            }
        }
    }
}