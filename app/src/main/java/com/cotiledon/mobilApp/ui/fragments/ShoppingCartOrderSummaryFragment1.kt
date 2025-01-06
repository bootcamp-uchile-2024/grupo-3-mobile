package com.cotiledon.mobilApp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.dataClasses.order.ShippingDetails
import com.cotiledon.mobilApp.ui.managers.OrderManager
import com.cotiledon.mobilApp.ui.managers.TokenManager

class ShoppingCartOrderSummaryFragment1 : Fragment() {

    private lateinit var nameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var rutEditText: EditText
    private lateinit var nextButton: Button
    private lateinit var modifyInfoText: TextView
    private lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shopping_cart_order_summary_layout_1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        tokenManager = TokenManager(requireContext())

        initializeViews(view)
        setupClickListeners()

        // Handle different user types
        if (tokenManager.isVisitor()) {
            handleVisitorFlow()
        } else {
            handleRegisteredUserFlow()
        }
    }

    private fun handleVisitorFlow() {
        // Start with empty fields for visitors
        clearFields()
        enableAllFields(true)

        // Check if we have previously entered visitor details
        OrderManager.getVisitorDetails()?.let { details ->
            populateFields(details)
        }
    }

    private fun handleRegisteredUserFlow() {
        // Load existing profile data and disable fields by default
        loadExistingDetails()
        enableAllFields(false)

        // Allow modification through the modify button
        modifyInfoText.visibility = View.VISIBLE
    }

    private fun populateFields(details: ShippingDetails) {
        nameEditText.setText(details.name)
        lastNameEditText.setText(details.lastName)
        emailEditText.setText(details.email)
        phoneEditText.setText(details.phone)
    }

    private fun clearFields() {
        nameEditText.text?.clear()
        lastNameEditText.text?.clear()
        emailEditText.text?.clear()
        phoneEditText.text?.clear()
        rutEditText.text?.clear()
    }

    private fun initializeViews(view: View) {
        // Update these IDs to match the layout
        nameEditText = view.findViewById(R.id.edit_text_name)
        lastNameEditText = view.findViewById(R.id.edit_text_lastname)
        emailEditText = view.findViewById(R.id.edit_text_email)
        phoneEditText = view.findViewById(R.id.edit_text_phone)
        rutEditText = view.findViewById(R.id.edit_text_rut)
        nextButton = view.findViewById(R.id.btn_siguiente)
        modifyInfoText = view.findViewById(R.id.text_view_information)
    }

    private fun setupClickListeners() {
        nextButton.setOnClickListener {
            if (validateInputs()) {
                saveShippingDetails()
                navigateToNextStep()
            }
        }

        modifyInfoText.setOnClickListener {
            enableAllFields(true)
        }
    }

    private fun enableAllFields(enabled: Boolean) {
        nameEditText.isEnabled = enabled
        lastNameEditText.isEnabled = enabled
        emailEditText.isEnabled = enabled
        phoneEditText.isEnabled = enabled
        rutEditText.isEnabled = enabled
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Validación de email
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        if (!emailEditText.text.toString().matches(emailPattern.toRegex())) {
            emailEditText.error = "Ingrese un email válido"
            isValid = false
        }

        // Validación de telefono
        val phonePattern = "^\\+?569\\d{8}$"
        if (!phoneEditText.text.toString().matches(phonePattern.toRegex())) {
            phoneEditText.error = "Ingrese un número válido (formato: +569XXXXXXXX)"
            isValid = false
        }

        // Validación de Nombre
        if (nameEditText.text.toString().length < 3) {
            nameEditText.error = "El nombre debe tener al menos 3 caracteres"
            isValid = false
        }

        return isValid
    }

    private fun saveShippingDetails() {
        val shippingDetails = ShippingDetails(
            name = nameEditText.text.toString(),
            lastName = lastNameEditText.text.toString(),
            email = emailEditText.text.toString(),
            phone = phoneEditText.text.toString(),
            address = "", // Will be filled in next fragment
            city = "",
            region = "",
            department = null,
            streetNumber = null
        )

        // For visitors, we store the details for later profile update
        if (tokenManager.isVisitor()) {
            OrderManager.saveVisitorDetails(shippingDetails)
        }

        // Save to OrderManager for the checkout process
        OrderManager.shippingDetails = shippingDetails
    }


    private fun loadExistingDetails() {
        OrderManager.shippingDetails?.let { details ->
            nameEditText.setText(details.name)
            lastNameEditText.setText(details.lastName)
            emailEditText.setText(details.email)
            phoneEditText.setText(details.phone)
        }
    }

    private fun enableEditing() {
        nameEditText.isEnabled = true
        lastNameEditText.isEnabled = true
        emailEditText.isEnabled = true
        phoneEditText.isEnabled = true
        rutEditText.isEnabled = true
    }

    private fun navigateToNextStep() {
        val nextFragment = ShoppingCartOrderSummaryFragment2.newInstance()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, nextFragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        fun newInstance() = ShoppingCartOrderSummaryFragment1()
    }
}