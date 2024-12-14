package com.cotiledon.mobilApp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cotiledon.mobilApp.R

class singin_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_singin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    //Función que lleva al home
    fun onSignInButtonClick(view: View) {
        val emailField = view.rootView.findViewById<EditText>(R.id.editText_singin_email)
        val passwordField = view.rootView.findViewById<EditText>(R.id.editText_singin_password)

        val email = emailField.text.toString()
        val password = passwordField.text.toString()

        if (email == "admin" && password == "1234") {
            val intent = Intent(view.context, MainAppHomeActivity::class.java)
            view.context.startActivity(intent)
        } else {
            Toast.makeText(view.context, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
        }
    }

    //Función que lleva a registro Login
    fun onRegisterTextClick(view: View) {
        val intent = Intent(this, UserRegistrationActivity::class.java)
        startActivity(intent)
    }

}




