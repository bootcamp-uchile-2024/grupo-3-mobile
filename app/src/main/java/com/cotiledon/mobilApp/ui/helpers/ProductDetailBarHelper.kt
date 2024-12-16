package com.cotiledon.mobilApp.ui.helpers

import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.cotiledon.mobilApp.R

class ProductDetailBarHelper(
    private val fragment: Fragment,
    private val searchCallback: SearchBarHelper.SearchCallback
) {
    private var backButton: ImageButton? = null
    // Initialize the SearchBarHelper with the fragment's activity
    private val searchBarHelper: SearchBarHelper =
        SearchBarHelper(fragment.requireActivity(), searchCallback)

    init {
        setupBackButton()
    }

    private fun setupBackButton() {
        // Find the back button in the included layout
        backButton = fragment.requireView().findViewById(R.id.btn_back)

        backButton?.setOnClickListener {
            // Use the fragment's parent fragment manager to handle back navigation
            fragment.parentFragmentManager.popBackStack()
        }
    }

    // Expose SearchBarHelper functions that might be needed
    fun clearSearch() {
        searchBarHelper.clearSearch()
    }

    fun setHint(hint: String) {
        searchBarHelper.setHint(hint)
    }

    fun setCameraButtonEnabled(enabled: Boolean) {
        searchBarHelper.setCameraButtonEnabled(enabled)
    }
}