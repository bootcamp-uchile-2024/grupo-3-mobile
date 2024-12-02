package com.cotiledon.mobilApp.ui.activities

import android.content.Intent
import android.os.Bundle
//import android.widget.Button
import androidx.activity.ComponentActivity
import com.cotiledon.mobilApp.R
import android.os.Handler
import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onRegisterTextClick(view: View) {
        val intent = Intent(this, UserRegistrationActivity::class.java)
        startActivity(intent)
    }

}
