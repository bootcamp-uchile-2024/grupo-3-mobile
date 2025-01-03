package com.cotiledon.mobilApp.ui.managers

import android.content.Context
import android.os.CountDownTimer

class TokenManager (context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_TOKE_FILE, Context.MODE_PRIVATE)
    private val editor = prefs.edit()
    private var expirationTimer: CountDownTimer? = null

    //Guardamos el token
    fun saveToken(token: String) {
        editor.putString(KEY_TOKEN, token)
        editor.apply()
    }

    //Obtenemos el token si existe
    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    //Iniciamos el timer para la expiración del token si la app pasa al background
    fun startExpirationTimer() {
        // Cancelar el timer anterior si existe
        expirationTimer?.cancel()

        expirationTimer = object : CountDownTimer(TOKEN_EXPIRATION_TIME, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                //Se deja vacío, solo se escribe para que quede explícito el miembro del objeto
            }

            override fun onFinish() {
                clearToken()
            }
        }.start()
    }

    fun cancelExpirationTimer() {
        expirationTimer?.cancel()
        expirationTimer = null
    }

    fun clearToken() {
        editor.clear()
        editor.apply()
        cancelExpirationTimer()
    }

    companion object {
        private const val PREFS_TOKE_FILE = "auth_prefs"
        private const val KEY_TOKEN = "auth_token"
        private const val TOKEN_EXPIRATION_TIME = 3600000L
    }
}