package com.cotiledon.mobilApp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cotiledon.mobilApp.R


class SignInActivity_unatended : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin_unattended)

        //Definición de vistas
        val signin_btn = findViewById<Button>(R.id.btnSingIn)
        val txt_username: TextView = findViewById(R.id.txtboxEmail)
        val txt_password: TextView = findViewById(R.id.txtboxPass)

        //Comprueba el usuario admin y lleva al home
        signin_btn.setOnClickListener{
            if (txt_username.text.toString() == "admin" && txt_password.text.toString() == "1234"){
                val intent2 = Intent(this, HomeActivity::class.java)
                startActivity(intent2)
            }
        }
    }
}