package com.cotiledon.mobilApp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.activities.MainContainerActivity
import com.cotiledon.mobilApp.ui.managers.CartStorageManager
import com.cotiledon.mobilApp.ui.managers.OrderManager

class ShoppingCartOrderSummaryFragment2 : Fragment() {
    private lateinit var regionSpinner: Spinner
    private lateinit var comunaSpinner: Spinner
    // Add new fields for street address components
    private lateinit var streetNameEditText: EditText
    private lateinit var streetNumberEditText: EditText
    private lateinit var departmentEditText: EditText  // For apartment/unit number
    private lateinit var referenceEditText: EditText   // For delivery instructions/reference
    private lateinit var receiverNameEditText: EditText
    private lateinit var finalizeButton: Button
    private lateinit var modifyInfoText: TextView

    //TODO: Hacer que esto se maneje con el backend. Esto es simplemente un ejemplo temporal
    private val regions = arrayOf("Región Metropolitana", "Valparaíso", "Biobío")
    private val comunasMap = mapOf(
        "Región Metropolitana" to arrayOf("Santiago", "Providencia", "Las Condes"),
        "Valparaíso" to arrayOf("Viña del Mar", "Valparaíso", "Quilpué"),
        "Biobío" to arrayOf("Concepción", "Talcahuano", "Chillán")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shopping_cart_order_summary_layout_2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupSpinners()
        setupClickListeners()
        loadExistingDetails()
    }

    private fun initializeViews(view: View) {
        regionSpinner = view.findViewById(R.id.spinner_region)
        comunaSpinner = view.findViewById(R.id.spinner_comuna)
        streetNameEditText = view.findViewById(R.id.street_name_edit_text)
        streetNumberEditText = view.findViewById(R.id.street_number_edit_text)
        departmentEditText = view.findViewById(R.id.department_edit_text)
        referenceEditText = view.findViewById(R.id.reference_edit_text)
        receiverNameEditText = view.findViewById(R.id.receiver_name_edit_text)
        finalizeButton = view.findViewById(R.id.btn_siguiente)
        modifyInfoText = view.findViewById(R.id.TextViewInformation)
    }

    private fun setupSpinners() {
        // Set up spinner de region
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            regions
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            regionSpinner.adapter = adapter
        }

        // Set up spinner de comuna con valor inicial
        updateComunaSpinner(regions[0])

        // Listener para cambiar valores de comunas cuando cambie la region
        regionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateComunaSpinner(regions[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateComunaSpinner(selectedRegion: String) {
        val comunas = comunasMap[selectedRegion] ?: arrayOf()
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            comunas
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            comunaSpinner.adapter = adapter
        }
    }

    private fun setupClickListeners() {
        finalizeButton.setOnClickListener {
            if (validateInputs()) {
                saveAddressDetails()
                navigateToPayment()
            }
        }

        modifyInfoText.setOnClickListener {
            enableEditing()
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (streetNameEditText.text.toString().isBlank()) {
            streetNameEditText.error = "Ingrese el nombre de la calle"
            isValid = false
        }

        if (streetNumberEditText.text.toString().isBlank()) {
            streetNumberEditText.error = "Ingrese el número"
            isValid = false
        }

        if (receiverNameEditText.text.toString().isBlank()) {
            receiverNameEditText.error = "Ingrese el nombre de quien recibe"
            isValid = false
        }

        return isValid
    }

    private fun saveAddressDetails() {
        val currentDetails = OrderManager.shippingDetails
        OrderManager.shippingDetails = currentDetails?.copy(
            address = streetNameEditText.text.toString(),
            city = comunaSpinner.selectedItem.toString(),
            region = regionSpinner.selectedItem.toString(),
            streetNumber = streetNumberEditText.text.toString(),
            department = departmentEditText.text.toString(),
            reference = referenceEditText.text.toString()
        )
    }

    @SuppressLint("SetTextI18n")
    private fun loadExistingDetails() {
        // Cargar los detalles de envío si ya existen
        OrderManager.shippingDetails?.let { details ->
            if (details.address.isNotEmpty()) {
                streetNameEditText.setText(details.address)
            }

            if (details.city.isNotEmpty()) {
                comunaSpinner.setSelection(comunasMap[details.region]?.indexOf(details.city) ?: 0)
            }

            if (details.region.isNotEmpty()) {
                regionSpinner.setSelection(regions.indexOf(details.region))
            }

            if (details.streetNumber != null) {
                streetNumberEditText.setText(details.streetNumber.toString())
            }

            if (details.department != null) {
                departmentEditText.setText(details.department)
            }

            if (details.reference?.isNotEmpty() == true) {
                referenceEditText.setText(details.reference)
            }
            // Settear el nombre de quien recibe. Utilizar el nombre original si no se llena
            receiverNameEditText.setText(details.name + " " + details.lastName)
        }
    }

    private fun enableEditing() {
        streetNameEditText.isEnabled = true
        streetNumberEditText.isEnabled = true
        departmentEditText.isEnabled = true
        referenceEditText.isEnabled = true
        receiverNameEditText.isEnabled = true
        regionSpinner.isEnabled = true
        comunaSpinner.isEnabled = true
    }

    private fun navigateToPayment() {
        if (validateInputs()) {
            saveAddressDetails()

            val paymentFragment = ShoppingCartFragmentPay.newInstance()

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, paymentFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    companion object {
        fun newInstance() = ShoppingCartOrderSummaryFragment2()
    }
}