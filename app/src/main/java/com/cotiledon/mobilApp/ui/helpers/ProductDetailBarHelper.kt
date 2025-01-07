package com.cotiledon.mobilApp.ui.helpers

import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.cotiledon.mobilApp.R

/**
 * Helper class that manages the common header bar functionality across fragments.
 * This includes search capabilities and back navigation, providing a consistent
 * user experience throughout the app.
 */
class ProductDetailBarHelper(
    private val fragment: Fragment,
    private val searchCallback: SearchBarHelper.SearchCallback
) {
    private var backButton: ImageButton? = null
    private var searchBarHelper: SearchBarHelper? = null

    init {
        // Initialize after fragment's view is created
        fragment.viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            fun onViewCreated(owner: LifecycleOwner, view: View) {
                setupViews(fragment.requireView())
            }
        })
    }

    private fun setupViews(rootView: View) {
        // Set up back button
        setupBackButton(rootView)

        // Set up search functionality
        setupSearchBar(rootView)
    }

    private fun setupBackButton(rootView: View) {
        backButton = rootView.findViewById<ImageButton>(R.id.btn_back)?.apply {
            setOnClickListener {
                fragment.parentFragmentManager.popBackStack()
            }
        }
    }

    private fun setupSearchBar(rootView: View) {
        // Find the search bar container view
        val searchBarView = rootView.findViewById<View>(R.id.searchbar_section)

        searchBarView?.let { view ->
            searchBarHelper = SearchBarHelper(view, searchCallback)
        }
    }

    /**
     * Public methods to control search bar behavior
     */
    fun clearSearch() {
        searchBarHelper?.clearSearch()
    }

    fun setHint(hint: String) {
        searchBarHelper?.setHint(hint)
    }

    fun setCameraButtonEnabled(enabled: Boolean) {
        searchBarHelper?.setCameraButtonEnabled(enabled)
    }

    /**
     * Methods to control back button visibility and behavior
     */
    fun setBackButtonVisible(visible: Boolean) {
        backButton?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setCustomBackAction(action: () -> Unit) {
        backButton?.setOnClickListener { action.invoke() }
    }

    companion object {
        private const val TAG = "ProductDetailBarHelper"
    }
}