package com.cotiledon.mobilApp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.adapters.CategoryAdapter
import com.cotiledon.mobilApp.ui.dataClasses.Category

class CategoriesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    //TODO: Implementar funcionalidad como recylcerview con categorías en forma de Grid de 2 columnas

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
            Category(getString(R.string.activity_categories_card_1), R.drawable.cat1),
            Category(getString(R.string.activity_categories_card_2), R.drawable.cat2),
            Category(getString(R.string.activity_categories_card_3), R.drawable.cat3),
            Category(getString(R.string.activity_categories_card_4), R.drawable.cat4),
            Category(getString(R.string.activity_categories_card_5), R.drawable.cat5),
            Category(getString(R.string.activity_categories_card_6),R.drawable.cat6)
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

        // Since we're using the same search bar across fragments, we should make its components accessible
        val searchEditText = searchContainer.findViewById<EditText>(1)  // The EditText is the second child
        val cameraButton = searchContainer.findViewById<ImageView>(2)   // The camera button is the third child

        searchEditText?.setOnClickListener {
            // Handle search functionality
            handleSearch()
        }

        cameraButton?.setOnClickListener {
            // Handle camera functionality
            handleCamera()
        }
    }

    private fun setupCategoryCards(view: View) {
        // Find the GridLayout that contains our category cards
        val gridLayout = view.findViewById<GridLayout>(R.id.gridLayout)

        // Set up click listeners for each card
        // We'll iterate through the GridLayout's children to set up each card
        for (i in 0 until gridLayout.childCount) {
            val cardView = gridLayout.getChildAt(i) as CardView

            cardView.setOnClickListener {
                // Handle the card click based on its position
                //handleCategoryCardClick(i)
            }
        }
    }

    private fun handleSearch() {
        // Implement search functionality
    }

    private fun handleCamera() {
        // Implement camera functionality
    }
}