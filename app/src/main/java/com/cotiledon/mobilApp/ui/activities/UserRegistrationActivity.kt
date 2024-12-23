package com.cotiledon.mobilApp.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.dataClasses.UserRegistration
import com.cotiledon.mobilApp.ui.retrofit.RetrofitUserClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserRegistrationActivity : AppCompatActivity() {

    private lateinit var contrasena: EditText
    private lateinit var contrasenaRepet: EditText
    private lateinit var nombre: EditText
    private lateinit var apellido: EditText
    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var telefono: EditText
    private lateinit var genero: Spinner
    private lateinit var rut: EditText
    private lateinit var checkbox: CheckBox
    private lateinit var buttonregister: Button
    private lateinit var birthDateButton: TextView
    private lateinit var birthDate: DatePicker

    private lateinit var  retrofitUserClient: RetrofitUserClient

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_registration)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        contrasena = findViewById(R.id.register_password)
        contrasenaRepet = findViewById(R.id.register_password_again)
        nombre = findViewById(R.id.register_name)
        apellido = findViewById(R.id.register_surname)
        username = findViewById(R.id.register_username)
        email = findViewById(R.id.register_email)
        telefono = findViewById(R.id.register_phone)
        genero = findViewById(R.id.register_gender)
        rut = findViewById(R.id.register_rut)
        checkbox = findViewById(R.id.register_checkbox)
        buttonregister = findViewById(R.id.register_create_btn)
        birthDateButton = findViewById(R.id.birth_date_bttn)
        birthDate = findViewById(R.id.birth_date)

        //Inicializar Retrofit Client
        retrofitUserClient = RetrofitUserClient.createUserClient()

        // Validación de Pass
        fun isValidPassword(password: String): Boolean {
            val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$"
            return password.matches(passwordRegex.toRegex())
        }

        // Validación de Nombre o Apellido (sólo permite letras y guines)
        fun isValidName(name: String): Boolean {
            val nameRegex = "^[A-Za-zÁ-ÿ-]+$"
            return name.matches(nameRegex.toRegex())
        }

        // Validación de Email
        fun isValidEmail(email: String): Boolean {
            val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$"
            return email.matches(emailRegex.toRegex())
        }

        fun calculateRutVerificationDigit(rut: String): String {
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

        // Validación de Rut
        fun isValidRut(rut: String): Boolean {

            val cleanRut = rut.replace(".", "").replace("-", "")

            // Validación de formato
            val rutRegex = "^\\d{7,8}[0-9K]$".toRegex()
            if (!cleanRut.matches(rutRegex)) return false

            // Separar entre número principal y verificador
            val body = cleanRut.substring(0, cleanRut.length - 1)
            val verificationDigit = cleanRut.last()

            // Calcular digito verificador
            val calculatedVerificationDigit = calculateRutVerificationDigit(body)

            return verificationDigit.toString() == calculatedVerificationDigit
        }

        fun isLeapYear(year: Int): Boolean {
            return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
        }

        // Validación Fecha de Nacimiento en formato ISO 8601
        fun isValidBirthDate(date: String): Boolean {
            // Regex para formato ISO 8601: YYYY-MM-DD
            val dateRegex = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$".toRegex()

            if (!date.matches(dateRegex)) return false

            try {
                val parts = date.split("-")
                val year = parts[0].toInt()
                val month = parts[1].toInt()
                val day = parts[2].toInt()

                // Validación de mes
                if (month < 1 || month > 12) return false

                // Validación de días según el mes
                val daysInMonth = when (month) {
                    1, 3, 5, 7, 8, 10, 12 -> 31
                    4, 6, 9, 11 -> 30
                    2 -> if (isLeapYear(year)) 29 else 28
                    else -> return false
                }

                // Validación de día dentro del rango del mes
                if (day < 1 || day > daysInMonth) return false

                // Validación de rango de año
                return year > 1900 && year <= Calendar.getInstance().get(Calendar.YEAR)
            } catch (e: Exception) {
                return false
            }
        }

        fun validateInputs(): Boolean {
            var isValid = true

            if (contrasena.text.toString() != contrasenaRepet.text.toString()) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                isValid = false
            }

            if(!isValidPassword(contrasena.text.toString())){
                contrasena.error = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un caracter especial"
                isValid = false
            }

            if (!isValidName(nombre.text.toString())) {
                nombre.error = "El nombre solo puede contener letras y guiones"
                isValid = false
            }

            if (!isValidName(apellido.text.toString())) {
                apellido.error = "El apellido solo puede contener letras y guiones"
                isValid = false
            }

            if (!isValidEmail(email.text.toString())) {
                email.error = "El correo electrónico no es válido"
                isValid = false
            }

            if (!isValidRut(rut.text.toString())) {
                rut.error = "El RUT no es válido"
                isValid = false
            }

            if (!isValidBirthDate(birthDateButton.text.toString())) {
                birthDateButton.error = "La fecha de nacimiento no es válida"
                isValid = false
            }

            if (!checkbox.isChecked) {
                checkbox.error = "Debes aceptar los terminos y condiciones"
                isValid = false
            }

            return isValid

            }

        fun registerUser() {
            // Crear objeto UserRegistration
            val userRegistration = UserRegistration(
                contrasena = contrasena.text.toString(),
                nombre = nombre.text.toString(),
                apellido = apellido.text.toString(),
                nombreUsuario = username.text.toString(),
                email = email.text.toString(),
                telefono = telefono.text.toString().takeIf { it.isNotBlank() }?.removePrefix("+"),
                genero = genero.selectedItem.toString().takeIf { it != "Género" },
                rut = rut.text.toString(),
                fechaNacimiento = birthDateButton.text.toString()
            )

            // Se lanza una corrutina
            lifecycleScope.launch {
                try {
                    // Se hace un llamado al servicio
                    val response = withContext(Dispatchers.IO) {
                        retrofitUserClient.registerUser(userRegistration)
                    }

                    // Se checkea la respuesta
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            // Registro exitoso
                            Toast.makeText(
                                this@UserRegistrationActivity,
                                "Registro exitoso",
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(Intent(this@UserRegistrationActivity, MainContainerActivity::class.java))
                            finish()
                        } else {
                            // Llamada no exitosa
                            val errorMessage = "${response.code()}: " + response.message()

                            Toast.makeText(
                                this@UserRegistrationActivity,
                                errorMessage,
                                Toast.LENGTH_LONG
                            ).show()

                            Log.e("UserRegistrationActivity", "Error: $errorMessage")

                        }
                    }
                } catch (e: Exception) {
                    // Manejo de excepciones
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@UserRegistrationActivity,
                            "Error: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()

                        val errorMessage = "Error: ${e.message}"
                        Log.e("UserRegistrationActivity", "Error: $errorMessage")
                    }
                }

            }
        }
        // Valida que los datos estén ok y si es así, envía el usuario a la base de datos
            buttonregister.setOnClickListener{
                if(validateInputs()){
                    registerUser()
                }
            }

        //Manejo del botón de fecha
        birthDateButton.setOnClickListener {
            if (birthDate.visibility == View.GONE) {
                birthDate.visibility = View.VISIBLE
        }else{
                birthDate.visibility = View.GONE
            }
        }

        birthDate.setOnDateChangedListener { _, year, month, day ->
            // Format the date in ISO 8601 (YYYY-MM-DD)
            val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
            birthDateButton.text = formattedDate
            birthDate.visibility = View.GONE
        }

        // Adaptador para spinner de género
        val genderAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.gender_items,
            R.layout.gender_spinner_item // Diseño predeterminado
        )

        genderAdapter.setDropDownViewResource(R.layout.gender_spinner_item)

        genero.adapter = genderAdapter

        genero.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    // Mensaje predeterminado seleccionado, no hacer nada
                    return
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada
            }
        }
    }
}