package com.cotiledon.mobilApp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signin)

        val signin_btn = findViewById<Button>(R.id.btnSingIn)
        val txt_username: TextView = findViewById(R.id.txtboxEmail)
        val txt_password:TextView = findViewById(R.id.txtboxPass)

        signin_btn.setOnClickListener{
            if (txt_username.text.toString() == "admin" && txt_password.text.toString() == "1234"){
                val intent2 = Intent(this,HomeActivity::class.java)
                startActivity(intent2)
            }
        }
    }
}