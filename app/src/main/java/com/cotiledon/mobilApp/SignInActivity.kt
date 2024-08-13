package com.cotiledon.mobilApp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SignInActivity : AppCompatActivity {

    constructor() : super()
    constructor(param: String) : super() {
        // Alguna lógica utilizando 'param'
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}