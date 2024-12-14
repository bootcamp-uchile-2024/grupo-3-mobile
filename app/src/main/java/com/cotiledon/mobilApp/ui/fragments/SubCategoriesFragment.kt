package com.cotiledon.mobilApp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.adapters.CategoryAdapter
import com.cotiledon.mobilApp.ui.dataClasses.Category
import com.cotiledon.mobilApp.ui.dataClasses.SubCategory

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

        // Initialize views
        recyclerView = view.findViewById(R.id.rv_subcategories)
        backButton = view.findViewById(R.id.btn_back)

        // Set up RecyclerView
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        // Sample data - replace with your actual subcategories
        val subcategories = listOf(
            Category("1", "Plantas De Interior", R.drawable.indoor_plants),
            Category("2", "Plantas De Exterior", R.drawable.outdoor_plants),
            Category("3", "Pet Friendly", R.drawable.pet_friendly),
            Category("4", "Productos Populares", R.drawable.popular_products),
            Category("5", "Plantas Bajo Mantenimiento", R.drawable.low_maintenance)
        )

        // Set up adapter
        val adapter = CategoryAdapter(subcategories) { category ->
            // Navigate to catalog with selected category
            navigateToCatalog(category)
        }
        recyclerView.adapter = adapter

        // Set up back button
        backButton.setOnClickListener {
            // Navigate back to categories
            parentFragmentManager.popBackStack()
        }
    }

    private fun navigateToCatalog(category: Category) {
        // We'll implement the CatalogFragment next
        val catalogFragment = CatalogFragment.newInstance(category.id)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, catalogFragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        fun newInstance() = SubCategoriesFragment()
    }
}