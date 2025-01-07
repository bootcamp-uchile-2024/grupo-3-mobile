package com.cotiledon.mobilApp.ui.fragments.base
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.cotiledon.mobilApp.ui.helpers.SearchBarHelper

/**
 * Base fragment class that provides search functionality.
 * Fragments that need search capabilities should extend this class
 * and implement the SearchBarHelper.SearchCallback interface.
 */
abstract class SearchableFragment : Fragment(), SearchBarHelper.SearchCallback {
    private var searchBarHelper: SearchBarHelper? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchBar(view)
    }

    private fun setupSearchBar(view: View) {
        searchBarHelper = SearchBarHelper(view, this)

        // Set default hint - can be overridden in child fragments
        searchBarHelper?.setHint(getSearchHint())

        // Configure camera button visibility
        searchBarHelper?.setCameraButtonEnabled(isCameraEnabled())
    }

    // Can be overridden by child fragments to customize the search hint
    protected open fun getSearchHint(): String {
        return "Buscar..."
    }

    // Can be overridden by child fragments to enable/disable camera
    protected open fun isCameraEnabled(): Boolean {
        return false
    }

    // Default implementations of SearchCallback methods
    override fun onQueryTextSubmit(query: String) {
        // To be implemented by child fragments
    }

    override fun onQueryTextChange(newText: String) {
        // To be implemented by child fragments
    }

    override fun onCameraButtonClick() {
        // To be implemented by child fragments
    }
}