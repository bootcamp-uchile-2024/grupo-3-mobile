package com.cotiledon.mobilApp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.cotiledon.mobilApp.R

class ShoppingCartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // We inflate the fragment's layout instead of setting it like we would in an Activity
        return inflater.inflate(R.layout.fragment_shopping_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Now that the view is created, we can set up our UI components
        setupSearchBar(view)
        setupShoppingCartContent(view)
    }

    private fun setupSearchBar(view: View) {
        // Find the search bar container
        val searchContainer = view.findViewById<LinearLayout>(R.id.activity_main_layout_1)

        // Use the actual IDs to find the views
        val searchEditText = view.findViewById<EditText>(R.id.search_edit_text)
        val cameraButton = view.findViewById<ImageView>(R.id.camera_button)

        searchEditText?.setOnClickListener {
            // Handle search functionality
        }

        cameraButton?.setOnClickListener {
            // Handle camera functionality
        }
    }

    private fun setupShoppingCartContent(view: View) {
        // Here you would set up any additional shopping cart specific content
        // For example, a RecyclerView for cart items, total price display, checkout button, etc.
    }
}