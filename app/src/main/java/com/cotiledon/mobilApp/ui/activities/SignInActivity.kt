package com.cotiledon.mobilApp.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.cotiledon.mobilApp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin_previous)

        //Splash screen
//        val btnStart = findViewById<Button>(R.id.btnStart)
//        btnStart.setOnClickListener {
//            val intent = Intent(this, SignInActivity::class.java)
//            startActivity(intent)
//        }

        val splashScreenDuration: Long = 3000 // 3 segundos

        lifecycleScope.launch {
            delay(splashScreenDuration)
            val intent = Intent(this@SignInActivity, MainContainerActivity::class.java)
            startActivity(intent)
            finish() // Cierra MainActivity para que no se mantenga en la pila
        }

    }
}




