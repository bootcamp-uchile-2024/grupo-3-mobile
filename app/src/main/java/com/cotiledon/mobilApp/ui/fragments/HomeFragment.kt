package com.cotiledon.mobilApp.ui.fragments

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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchBar(view)
        setupCategories(view)
        setupBanners(view)
        setupBottomButtons(view)
    }

    private fun setupSearchBar(view: View) {
        val searchEditText = view.findViewById<EditText>(R.id.search_edit_text)
        val cameraButton = view.findViewById<ImageView>(R.id.camera_button)

        searchEditText.setOnClickListener {
        }

        cameraButton.setOnClickListener {
        }
    }

    //TODO: Manejo de selección de categoría
    private fun setupCategories(view: View) {
        val categoryAll = view.findViewById<TextView>(R.id.category_all)
        val categoryHighlighted = view.findViewById<TextView>(R.id.category_highlited)
        val categoryAccessories = view.findViewById<TextView>(R.id.category_accesories)
        val categorySupplies = view.findViewById<TextView>(R.id.category_supplies)

        categoryAll.setOnClickListener {
        }

        categoryHighlighted.setOnClickListener {
        }

        categoryAccessories.setOnClickListener {
        }

        categorySupplies.setOnClickListener {
        }
    }

    //TODO: Manejo de lógica de banners
    private fun setupBanners(view: View) {
        val upperBanner = view.findViewById<ImageView>(R.id.imageView_banner_up)
        val lowerBanner = view.findViewById<ImageView>(R.id.activity_main_banner_down)

        upperBanner.setOnClickListener {
        }

        lowerBanner.setOnClickListener {
        }
    }

    //TODO: Manejo de botones inferiores
    private fun setupBottomButtons(view: View) {
        val plantAiButton = view.findViewById<Button>(R.id.app_plant_ai_button)
        val benefitsButton = view.findViewById<Button>(R.id.benefits_button)
        val tipsButton = view.findViewById<Button>(R.id.tips_button)

        plantAiButton.setOnClickListener {
        }

        benefitsButton.setOnClickListener {
        }

        tipsButton.setOnClickListener {
        }
    }

    //TODO: Recycler view de recomendados
    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.activity_main_recyclerview)
    }
}