package com.cotiledon.mobilApp.ui.helpers


import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.cotiledon.mobilApp.R
import android.view.View
import android.widget.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Helper class to manage search functionality across fragments.
 * This class handles the search bar UI interactions and delegates search actions
 * to the implementing fragment through callbacks.
 */
class SearchBarHelper(
    private val rootView: View,
    private val searchCallback: SearchCallback
) {
    private var searchEditText: EditText? = null
    private var cameraButton: ImageView? = null
    private var debounceJob: Job? = null
    private val DEBOUNCE_DELAY = 300L // Milliseconds

    // Interface for search-related callbacks
    interface SearchCallback {
        fun onQueryTextSubmit(query: String)
        fun onQueryTextChange(newText: String)
        fun onCameraButtonClick()
    }

    // Get the CoroutineScope from the root view's context
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        initializeViews()
        setupSearchListeners()
    }

    private fun initializeViews() {
        // Initialize views using the provided root view
        searchEditText = rootView.findViewById(R.id.search_edit_text)
        cameraButton = rootView.findViewById(R.id.camera_button)
    }
    private fun setupSearchListeners() {
        searchEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Cancel previous debounce job if it exists
                debounceJob?.cancel()

                // Create new debounce job
                debounceJob = scope.launch {
                    delay(DEBOUNCE_DELAY)
                    s?.toString()?.let { searchCallback.onQueryTextChange(it) }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Set up search action listener
        searchEditText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchEditText?.text?.toString()?.let {
                    searchCallback.onQueryTextSubmit(it)
                }
                true
            } else {
                false
            }
        }

        // Set up camera button listener
        cameraButton?.setOnClickListener {
            searchCallback.onCameraButtonClick()
        }
    }

    fun cleanup() {
        scope.cancel()
    }

    /**
     * Clears the current search text and resets the search state
     */
    fun clearSearch() {
        searchEditText?.apply {
            setText("")
            clearFocus()
        }
    }

    /**
     * Sets the hint text for the search EditText
     */
    fun setHint(hint: String) {
        searchEditText?.hint = hint
    }

    /**
     * Enables or disables the camera button
     */
    fun setCameraButtonEnabled(enabled: Boolean) {
        cameraButton?.apply {
            isEnabled = enabled
            alpha = if (enabled) 1.0f else 0.5f
        }
    }

    /**
     * Returns the current search text
     */
    fun getCurrentSearchText(): String {
        return searchEditText?.text?.toString() ?: ""
    }
}