package com.cotiledon.mobilApp.ui.activities

import android.os.Bundle
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
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.dataClasses.UserRegistration

class UserRegistrationActivity : AppCompatActivity() {

    private lateinit var contrasena: EditText
    private lateinit var contrasenaRepet: EditText
    private lateinit var nombre: EditText
    private lateinit var apellido: EditText
    private lateinit var email: EditText
    private lateinit var telefono: EditText
    private lateinit var genero: Spinner
    private lateinit var rut: EditText
    private lateinit var checkbox: CheckBox
    private lateinit var buttonregister: Button
    private lateinit var birthDateButton: TextView
    private lateinit var birthDate: DatePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_registration)

        contrasena = findViewById(R.id.register_password)
        contrasenaRepet = findViewById(R.id.register_password_again)
        nombre = findViewById(R.id.register_name)
        apellido = findViewById(R.id.register_surname)
        email = findViewById(R.id.register_email)
        telefono = findViewById(R.id.register_phone)
        genero = findViewById(R.id.register_gender)
        rut = findViewById(R.id.register_rut)
        checkbox = findViewById(R.id.register_checkbox)
        buttonregister = findViewById(R.id.register_create_btn)
        birthDateButton = findViewById(R.id.birth_date_bttn)
        birthDate = findViewById(R.id.birth_date)

        /*buttonregister.setOnClickListener{
            if(validateInputs()){
                val userRegistration = UserRegistration(
                    contrasena = contrasena.text.toString(),
                    nombre = nombre.text.toString(),
                    apellido = apellido.text.toString(),
                    email = email.text.toString(),
                    telefono = telefono.text.toString(),
                    genero = genero.selectedItem.toString(),
                    rut = rut.text.toString(),
                    birthDate = birthDate.selectedDate.toString()
                )
            }
        }*/

        birthDateButton.setOnClickListener {
            if (birthDate.visibility == View.GONE) {
                birthDate.visibility = View.VISIBLE
        }else{
                birthDate.visibility = View.GONE
            }
        }

        birthDate.setOnDateChangedListener{_, year, month, day ->
            val formattedDate = "$day/${month+1}/$year"
            birthDateButton.text = formattedDate
            birthDate.visibility = View.GONE
        }


        val genderOptions = listOf("Género", "Masculino", "Femenino", "Prefiero no decir")


        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_item, // Diseño predeterminado
            genderOptions
        )

        adapter.setDropDownViewResource(R.layout.spinner_item)

        genero.adapter = adapter

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
                // Realiza acciones según la opción seleccionada
                val selectedGender = genderOptions[position]
                return
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada
            }
        }
    }
}