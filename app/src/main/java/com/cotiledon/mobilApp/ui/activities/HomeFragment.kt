package com.cotiledon.mobilApp.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment's layout
        return inflater.inflate(R.layout.activity_home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize search functionality
        setupSearchBar(view)

        // Initialize category selection
        setupCategories(view)

        // Initialize banners
        setupBanners(view)

        // Initialize bottom section buttons
        setupBottomButtons(view)
    }

    private fun setupSearchBar(view: View) {
        // Find and set up the search bar components
        val searchEditText = view.findViewById<EditText>(R.id.search_edit_text)
        val cameraButton = view.findViewById<ImageView>(R.id.camera_button)

        searchEditText.setOnClickListener {
            // Handle search bar click - You might want to expand the search functionality
        }

        cameraButton.setOnClickListener {
            // Handle camera button click - Maybe open camera functionality
        }
    }

    private fun setupCategories(view: View) {
        // Find and set up all category buttons
        val categoryAll = view.findViewById<TextView>(R.id.category_all)
        val categoryHighlighted = view.findViewById<TextView>(R.id.category_highlited)
        val categoryAccessories = view.findViewById<TextView>(R.id.category_accesories)
        val categorySupplies = view.findViewById<TextView>(R.id.category_supplies)

        // Set up click listeners for each category
        categoryAll.setOnClickListener {
            // Handle "All" category selection
        }

        categoryHighlighted.setOnClickListener {
            // Handle "Highlighted" category selection
        }

        categoryAccessories.setOnClickListener {
            // Handle "Accessories" category selection
        }

        categorySupplies.setOnClickListener {
            // Handle "Supplies" category selection
        }
    }

    private fun setupBanners(view: View) {
        // Find and set up banner images
        val upperBanner = view.findViewById<ImageView>(R.id.imageView_banner_up)
        val lowerBanner = view.findViewById<ImageView>(R.id.activity_main_banner_down)

        upperBanner.setOnClickListener {
            // Handle upper banner click
        }

        lowerBanner.setOnClickListener {
            // Handle lower banner click
        }
    }

    private fun setupBottomButtons(view: View) {
        // Find and set up all bottom section buttons
        val plantAiButton = view.findViewById<Button>(R.id.app_plant_ai_button)
        val benefitsButton = view.findViewById<Button>(R.id.benefits_button)
        val tipsButton = view.findViewById<Button>(R.id.tips_button)

        plantAiButton.setOnClickListener {
            // Handle Plant AI button click
        }

        benefitsButton.setOnClickListener {
            // Handle Benefits button click
        }

        tipsButton.setOnClickListener {
            // Handle Tips button click
        }
    }

    // If you have a RecyclerView, you'll need to set it up as well
    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.activity_main_recyclerview)
        // Set up your RecyclerView here - layout manager, adapter, etc.
    }
}