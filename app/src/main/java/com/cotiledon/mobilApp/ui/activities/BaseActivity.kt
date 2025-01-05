package com.cotiledon.mobilApp.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.backend.authentication.RetrofitAuthClient
import com.cotiledon.mobilApp.ui.fragments.SignInFragment
import com.cotiledon.mobilApp.ui.managers.TokenManager
import kotlinx.coroutines.launch

abstract class BaseActivity : AppCompatActivity() {
    lateinit var tokenManager: TokenManager
    private var isFinishing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(this)

        tokenManager.setTokenRefreshCallback {
            lifecycleScope.launch {
                //refreshToken()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        //Se empieza el timer cuando la app pasa al background
        tokenManager.setAppForegroundState(false)
        if (!isFinishing) {
            tokenManager.startExpirationTimer()
        }
    }

    override fun onResume() {
        super.onResume()
        //Se cancela cuando la app pasa al foreground
        tokenManager.setAppForegroundState(true)
        tokenManager.cancelExpirationTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        //Se limpia el token cuando la app se destruye
        if (isFinishing) {
            tokenManager.clearAuthData()
        }
    }

    //TODO: A implementarse luego de que exista el servicio de refrescar el token
    /*private suspend fun refreshToken() {
        try {
            val authClient = RetrofitAuthClient.createAuthClient()
            val currentToken = tokenManager.getToken()

            if (currentToken != null) {
                val response = authClient.refreshToken(currentToken)

                if (response.isSuccessful) {
                    response.body()?.let { authResponse ->
                        tokenManager.saveAuthData(
                            authResponse.access_token,
                            authResponse.user_id,
                            authResponse.token_expiration
                        )
                    }
                } else {
                    tokenManager.clearAuthData()
                    redirectToLogin()
                }
            }
        } catch (e: Exception) {
            tokenManager.clearAuthData()
            redirectToLogin()
        }
    }*/

    private fun redirectToLogin() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SignInFragment())
            .commit()
    }

}