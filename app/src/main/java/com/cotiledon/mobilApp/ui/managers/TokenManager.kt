package com.cotiledon.mobilApp.ui.managers

import android.content.Context
import android.os.CountDownTimer

class TokenManager (context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_TOKEN_FILE, Context.MODE_PRIVATE)
    private val editor = prefs.edit()
    private var expirationTimer: CountDownTimer? = null
    private var tokenExpirationTimer: CountDownTimer? = null

    //Guardamos toda la información del token
    fun saveAuthData(token: String, userId: Int, tokenExpiration: Long) {
        editor.apply {
            putString(KEY_TOKEN, token)
            putInt(KEY_USER_ID, userId)
            putLong(KEY_TOKEN_EXPIRATION, tokenExpiration)
            apply()
        }
        startTokenExpirationTimer(tokenExpiration)
    }

    //Obtenemos el token si existe
    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    //Obtener el id del usuario
    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }

        //Iniciamos el timer para la expiración del token si la app pasa al background
    fun startExpirationTimer() {
        // Cancelar el timer anterior si existe
        expirationTimer?.cancel()

        // Iniciar el nuevo timer con 1 hr
        expirationTimer = object : CountDownTimer(BACKGROUND_EXPIRATION_TIME, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                //Se deja vacío, solo se escribe para que quede explícito el miembro del objeto
            }

            override fun onFinish() {
                clearAuthData()
            }
        }.start()
    }


    //Iniciamos el timer de expiración del token traido desde el servidor
    private fun startTokenExpirationTimer(expirationTime: Long) {
        tokenExpirationTimer?.cancel()

        val currentTime = System.currentTimeMillis()
        val timeUntilExpiration = expirationTime - currentTime

        if (timeUntilExpiration > 0) {
            tokenExpirationTimer = object : CountDownTimer(timeUntilExpiration, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    //Cuando le queden 5 minutos de expiración, se vuelve a traer el token
                    if (millisUntilFinished <= REFRESH_THRESHOLD && isAppInForeground) {
                        refreshTokenCallback?.invoke()
                    }
                }

                override fun onFinish() {
                    clearAuthData()
                }
            }.start()
        }
    }

    //Cancelar el timer de 1 hora (para cuando la app pasa de vuelta al foreground)
    fun cancelExpirationTimer() {
        expirationTimer?.cancel()
        expirationTimer = null
    }

    //Función para limpiar la data del token
    fun clearAuthData() {
        editor.clear().apply()
        cancelExpirationTimer()
        tokenExpirationTimer?.cancel()
        tokenExpirationTimer = null
    }

    // Callback para refrescar el token
    private var refreshTokenCallback: (() -> Unit)? = null
    fun setTokenRefreshCallback(callback: () -> Unit) {
        refreshTokenCallback = callback
    }

    //Trackeo para saber si la app esta en foreground
    private var isAppInForeground = true
    fun setAppForegroundState(inForeground: Boolean) {
        isAppInForeground = inForeground
    }

    companion object {
        private const val PREFS_TOKEN_FILE = "auth_prefs"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_TOKEN_EXPIRATION = "token_expiration"
        private const val BACKGROUND_EXPIRATION_TIME = 3600000L //1 hora en milisegundos
        private const val REFRESH_THRESHOLD = 300000L //5 minutos
    }
}