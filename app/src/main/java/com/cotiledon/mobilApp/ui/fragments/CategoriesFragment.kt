package com.cotiledon.mobilApp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.adapters.CategoryAdapter
import com.cotiledon.mobilApp.ui.dataClasses.catalog.Category

class CategoriesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchBar(view)
        setupRecyclerView(view)
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)

        //Grid layout
        val gridLayoutManager = GridLayoutManager(context, 2)
        recyclerView.layoutManager = gridLayoutManager

        //Lista fija de categorías manejado localmente
        val categories = listOf(
            Category(getString(R.string.activity_categories_card_1), R.drawable.cat1,1),
            Category(getString(R.string.activity_categories_card_2), R.drawable.cat2,2),
            Category(getString(R.string.activity_categories_card_3), R.drawable.cat3,3),
            Category(getString(R.string.activity_categories_card_4), R.drawable.cat4,4),
            Category(getString(R.string.activity_categories_card_5), R.drawable.cat5,5),
            Category(getString(R.string.activity_categories_card_6), R.drawable.cat6,6)
        )

        //Adaptador con manejo de click
        categoryAdapter = CategoryAdapter(categories) { category ->
            navigateToSubCategories(category)
        }

        recyclerView.adapter = categoryAdapter
    }

    private fun navigateToSubCategories(category: Category) {
        //Creación de argumentos a ser enviados como Bundle
        val args = SubCategoriesFragment.createArguments(
            category.name,
            category.id
        )

        //Crear fragment y pasar argumentos del bundle
        val subCategoriesFragment = SubCategoriesFragment.newInstance(args)

        //Navegar al fragment de subcategoría
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, subCategoriesFragment)
            //Se agrega este fragment al backstack
            .addToBackStack(null)
            .commit()
    }


    private fun setupSearchBar(view: View) {
        val searchContainer = view.findViewById<LinearLayout>(R.id.searchbar_section)

        // Ya que estamos usando la misma searchbar por todos los fragmetos, conviene hacerla accesible globalmente
        val searchEditText = searchContainer.findViewById<EditText>(1)
        val cameraButton = searchContainer.findViewById<ImageView>(2)

        searchEditText?.setOnClickListener {
            handleSearch()
        }

        cameraButton?.setOnClickListener {
            handleCamera()
        }
    }

    private fun setupCategoryCards(view: View) {
        val gridLayout = view.findViewById<GridLayout>(R.id.recyclerView)

        for (i in 0 until gridLayout.childCount) {
            val cardView = gridLayout.getChildAt(i) as CardView

            cardView.setOnClickListener {
            }
        }
    }

    private fun handleSearch() {
        //TODO: Implementar lógica de búsqueda
    }

    private fun handleCamera() {
        //TODO: Implementar lógica de cámara
    }
}