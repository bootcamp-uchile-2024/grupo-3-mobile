package com.cotiledon.mobilApp.ui.helpers

import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.cotiledon.mobilApp.R

//Helper para header general
class ProductDetailBarHelper(
    private val fragment: Fragment,
    private val searchCallback: SearchBarHelper.SearchCallback
) {
    private var backButton: ImageButton? = null
    private val searchBarHelper: SearchBarHelper =
        SearchBarHelper(fragment.requireActivity(), searchCallback)

    init {
        setupBackButton()
    }

    private fun setupBackButton() {
        backButton = fragment.requireView().findViewById(R.id.btn_back)

        backButton?.setOnClickListener {
            fragment.parentFragmentManager.popBackStack()
        }
    }

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