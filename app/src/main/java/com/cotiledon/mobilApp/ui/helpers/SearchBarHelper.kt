package com.cotiledon.mobilApp.ui.helpers

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import com.cotiledon.mobilApp.R

class SearchBarHelper(
    private val activity: Activity,
    private val searchCallback: SearchCallback
) {
    private var searchEditText: EditText? = null
    private var cameraButton: ImageButton? = null

    //Interface para manejo de acciones del searchbar
    interface SearchCallback {
        fun onQueryTextSubmit(query: String)
        fun onQueryTextChange(newText: String)
        fun onCameraButtonClick()
    }

    init {
        setupSearchView()
    }

    private fun setupSearchView() {

        searchEditText = activity.findViewById(R.id.search_edit_text)
        cameraButton = activity.findViewById(R.id.camera_button)

        //Listner para cambios en el texto
        searchEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.toString()?.let { searchCallback.onQueryTextChange(it) }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        //Listener para envío de texto
        searchEditText?.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                searchEditText?.text?.toString()?.let {
                    searchCallback.onQueryTextSubmit(it)
                }
                true
            } else {
                false
            }
        }

        //Listener para botón de cámara
        cameraButton?.setOnClickListener {
            searchCallback.onCameraButtonClick()
        }
    }

    //Limpiar búsqueda
    fun clearSearch() {
        searchEditText?.apply {
            setText("")
            clearFocus()
        }
    }

    //Establecer hint
    fun setHint(hint: String) {
        searchEditText?.hint = hint
    }

    //Posibilidad de activar o desactivar botón de cámara
    fun setCameraButtonEnabled(enabled: Boolean) {
        cameraButton?.isEnabled = enabled
        cameraButton?.alpha = if (enabled) 1.0f else 0.5f
    }
}