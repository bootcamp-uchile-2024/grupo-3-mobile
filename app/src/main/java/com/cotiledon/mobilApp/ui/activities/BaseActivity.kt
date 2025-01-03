package com.cotiledon.mobilApp.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cotiledon.mobilApp.ui.managers.TokenManager

abstract class BaseActivity : AppCompatActivity() {
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(this)
    }

    override fun onPause() {
        super.onPause()
        //Se empieza el timer cuando la app pasa al background
        tokenManager.startExpirationTimer()
    }

    override fun onResume() {
        super.onResume()
        //Se cancela cuando la app pasa al foreground
        tokenManager.cancelExpirationTimer()
    }

    override fun onStop() {
        super.onStop()
        //Se limpia el token cuando la app se cierra
        tokenManager.clearToken()
    }

    override fun onDestroy() {
        super.onDestroy()
        //Se limpia el token cuando la app se destruye
        tokenManager.clearToken()
    }
}