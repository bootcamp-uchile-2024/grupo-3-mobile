package com.cotiledon.mobilApp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import com.cotiledon.mobilApp.R

class CategoriesFragment : Fragment() {

    // We'll use these constants to identify our category cards
    companion object {
        private const val PLANTS_CARD = 0
        private const val COMMUNITY_CARD = 1
        private const val EDUCATION_CARD = 2
        private const val VIRTUAL_ASSISTANT_CARD = 3
        private const val SELL_WITH_US_CARD = 4
        private const val ABOUT_US_CARD = 5
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment's layout
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the different components of our UI
        setupSearchBar(view)
        setupCategoryCards(view)
    }

    private fun setupSearchBar(view: View) {
        // Find the search layout container
        val searchContainer = view.findViewById<LinearLayout>(R.id.activity_main_layout_1)

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

    /*private fun handleCategoryCardClick(position: Int) {
        // Navigate to the appropriate screen based on which card was clicked
        when (position) {
            PLANTS_CARD -> navigateToPlants()
            COMMUNITY_CARD -> navigateToCommunity()
            EDUCATION_CARD -> navigateToEducation()
            VIRTUAL_ASSISTANT_CARD -> navigateToVirtualAssistant()
            SELL_WITH_US_CARD -> navigateToSellWithUs()
            ABOUT_US_CARD -> navigateToAboutUs()
        }
    }*/

    // Navigation methods for each category
    /*private fun navigateToPlants() {
        // Navigate to Plants section using Fragment transaction
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PlantsFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToCommunity() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CommunityFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToEducation() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, EducationFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToVirtualAssistant() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, VirtualAssistantFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToSellWithUs() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SellWithUsFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToAboutUs() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, AboutUsFragment())
            .addToBackStack(null)
            .commit()
    }*/

    private fun handleSearch() {
        // Implement search functionality
    }

    private fun handleCamera() {
        // Implement camera functionality
    }
}