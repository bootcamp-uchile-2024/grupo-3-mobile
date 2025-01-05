package com.cotiledon.mobilApp.ui.fragments

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.dataClasses.profile.UserRegistrationData

class UserRegisterFragment : Fragment() {
    private lateinit var nameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var passwordConfirmEditText: EditText
    private lateinit var rutEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var genderSpinner: Spinner
    private lateinit var birthdayEditText: EditText
    private lateinit var nextButton: Button

    // Data holder for registration info
    private val registrationData = UserRegistrationData(idRol = 3)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupSpinner()
        setupClickListeners()
    }

    private fun initializeViews(view: View) {
        nameEditText = view.findViewById(R.id.edittext_name)
        lastNameEditText = view.findViewById(R.id.edittext_lastname)
        usernameEditText = view.findViewById(R.id.edittext_username)
        emailEditText = view.findViewById(R.id.edittext_email)
        passwordEditText = view.findViewById(R.id.edittext_password)
        passwordConfirmEditText = view.findViewById(R.id.edittext_password_confirm)
        rutEditText = view.findViewById(R.id.edittext_rut)
        phoneEditText = view.findViewById(R.id.edittext_phone)
        genderSpinner = view.findViewById(R.id.spinner_gender)
        birthdayEditText = view.findViewById(R.id.edittext_birthday)
        nextButton = view.findViewById(R.id.button_register_next)
    }

    // Update setupClickListeners to add more robust error handling
    private fun setupClickListeners() {
        nextButton.setOnClickListener {
            try {
                if (validateInputs()) {
                    saveRegistrationData()
                    navigateToNextStep()
                }
            } catch (e: Exception) {
                Log.e("UserRegisterFragment", "Error in validation: ${e.message}")
                Toast.makeText(
                    context,
                    "Error validando los datos: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        birthdayEditText.setOnClickListener {
            showDatePicker()
        }
    }

    private fun setupSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_items,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            genderSpinner.adapter = adapter
        }
    }

    // Update the validateInputs function with comprehensive validation
    private fun validateInputs(): Boolean {
        var isValid = true

        // Password validation
        if (passwordEditText.text.toString() != passwordConfirmEditText.text.toString()) {
            Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (!isValidPassword(passwordEditText.text.toString())) {
            passwordEditText.error = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un caracter especial"
            isValid = false
        }

        // Name validation
        if (!isValidName(nameEditText.text.toString())) {
            nameEditText.error = "El nombre solo puede contener letras y guiones"
            isValid = false
        }

        if (!isValidName(lastNameEditText.text.toString())) {
            lastNameEditText.error = "El apellido solo puede contener letras y guiones"
            isValid = false
        }

        // Email validation
        if (!isValidEmail(emailEditText.text.toString())) {
            emailEditText.error = "El correo electrónico no es válido"
            isValid = false
        }

        // RUT validation
        if (!isValidRut(rutEditText.text.toString())) {
            rutEditText.error = "El RUT no es válido"
            isValid = false
        }

        // Birthday validation
        if (!isValidBirthDate(birthdayEditText.text.toString())) {
            birthdayEditText.error = "La fecha de nacimiento no es válida"
            isValid = false
        }

        // Gender validation
        if (genderSpinner.selectedItemPosition == 0) {
            (genderSpinner.selectedView as? TextView)?.error = "Debe seleccionar un género"
            isValid = false
        }

        return isValid
    }

    private fun isValidPassword(password: String): Boolean {
        // Password must have at least 8 characters, one uppercase, one lowercase,
        // one number and one special character
        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$"
        return password.matches(passwordRegex.toRegex())
    }

    private fun isValidName(name: String): Boolean {
        // Only letters and hyphens allowed
        val nameRegex = "^[A-Za-zÁ-ÿ-]+$"
        return name.matches(nameRegex.toRegex())
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$"
        return email.matches(emailRegex.toRegex())
    }

    private fun calculateRutVerificationDigit(rut: String): String {
        var total = 0
        var multiplier = 2

        for (i in rut.length - 1 downTo 0) {
            total += rut[i].toString().toInt() * multiplier
            multiplier = if (multiplier == 7) 2 else multiplier + 1
        }

        return when (val remainder = 11 - (total % 11)) {
            10 -> "K"
            11 -> "0"
            else -> remainder.toString()
        }
    }

    private fun isValidRut(rut: String): Boolean {
        val cleanRut = rut.replace(".", "").replace("-", "")

        // Format validation
        val rutRegex = "^\\d{7,8}[0-9K]$".toRegex()
        if (!cleanRut.matches(rutRegex)) return false

        // Split between main number and verification digit
        val body = cleanRut.substring(0, cleanRut.length - 1)
        val verificationDigit = cleanRut.last()

        // Calculate verification digit
        val calculatedVerificationDigit = calculateRutVerificationDigit(body)

        return verificationDigit.toString() == calculatedVerificationDigit
    }

    private fun isLeapYear(year: Int): Boolean {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
    }

    private fun isValidBirthDate(date: String): Boolean {
        // ISO 8601 format: YYYY-MM-DD
        val dateRegex = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$".toRegex()

        if (!date.matches(dateRegex)) return false

        try {
            val parts = date.split("-")
            val year = parts[0].toInt()
            val month = parts[1].toInt()
            val day = parts[2].toInt()

            // Month validation
            if (month < 1 || month > 12) return false

            // Days validation according to month
            val daysInMonth = when (month) {
                1, 3, 5, 7, 8, 10, 12 -> 31
                4, 6, 9, 11 -> 30
                2 -> if (isLeapYear(year)) 29 else 28
                else -> return false
            }

            // Day validation within month range
            if (day < 1 || day > daysInMonth) return false

            // Year range validation
            return year > 1900 && year <= Calendar.getInstance().get(Calendar.YEAR)
        } catch (e: Exception) {
            return false
        }
    }


    private fun saveRegistrationData() {
        registrationData.apply {
            nombre = nameEditText.text.toString()
            apellido = lastNameEditText.text.toString()
            nombreUsuario = usernameEditText.text.toString()
            email = emailEditText.text.toString()
            telefono = phoneEditText.text.toString()
            genero = genderSpinner.selectedItem.toString()
            rut = rutEditText.text.toString()
            fechaNacimiento = birthdayEditText.text.toString()
            contrasena = passwordEditText.text.toString()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                // Format the date in ISO 8601 (YYYY-MM-DD)
                val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
                birthdayEditText.setText(formattedDate)

                // Validate the date immediately
                if (!isValidBirthDate(formattedDate)) {
                    birthdayEditText.error = "Fecha de nacimiento inválida"
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            // Set max date to today
            datePicker.maxDate = System.currentTimeMillis()
            // Set min date to 1900
            calendar.set(1900, 0, 1)
            datePicker.minDate = calendar.timeInMillis
        }.show()
    }

    private fun navigateToNextStep() {
        val nextFragment = UserRegisterNextFragment.newInstance(registrationData)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, nextFragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        fun newInstance() = UserRegisterFragment()
    }
}