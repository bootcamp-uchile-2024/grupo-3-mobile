package com.cotiledon.mobilApp.ui.managers

import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import com.cotiledon.mobilApp.ui.dataClasses.profile.VisitorResponse
import java.io.File

class TokenManager (private val context: Context) {
    private val appContext = context.applicationContext
    private val prefs by lazy {
        appContext.getSharedPreferences(PREFS_TOKEN_FILE, Context.MODE_PRIVATE)
    }

    private val editor by lazy {
        prefs.edit()
    }

    private var expirationTimer: CountDownTimer? = null
    private var tokenExpirationTimer: CountDownTimer? = null

    //Guardamos toda la información del token
    fun saveAuthData(token: String, userId: Int, tokenExpiration: Long) {
        Log.d("TokenManager", "Starting save operation...")
        Log.d("TokenManager", "Current prefs content before save: ${prefs.all}")

        try {
            // Clear existing data first
            editor.clear()
            editor.commit()

            // Save new data
            editor.putString(KEY_TOKEN, token)
            editor.putInt(KEY_USER_ID, userId)
            editor.putLong(KEY_TOKEN_EXPIRATION, tokenExpiration)

            val committed = editor.commit()
            Log.d("TokenManager", "Save committed: $committed")

            // Verify immediate save
            val savedToken = prefs.getString(KEY_TOKEN, null)
            Log.d("TokenManager", "Immediate verification - Saved token: $savedToken")

            if (savedToken != token) {
                Log.e("TokenManager", "Token verification failed! Expected: $token, Got: $savedToken")
            }

            startTokenExpirationTimer(tokenExpiration)
            verifyPreferencesFile()

        } catch (e: Exception) {
            Log.e("TokenManager", "Error saving auth data", e)
        }
    }

    fun isVisitor(): Boolean {
        val role = prefs.getString(KEY_USER_ROLE, null)
        val userId = prefs.getInt(KEY_USER_ID, -1)
        return role == ROLE_VISITOR && userId != -1
    }

    fun saveVisitorAuthData(visitorResponse: VisitorResponse) {
        // Don't overwrite existing visitor data if we already have it
        if (!isVisitor()) {
            editor.apply {
                putString(KEY_TOKEN, visitorResponse.access_token)
                putInt(KEY_USER_ID, visitorResponse.id)
                putString(KEY_USER_ROLE, visitorResponse.rol)
                putLong(KEY_TOKEN_EXPIRATION, visitorResponse.expToken)
                commit()  // Use commit() instead of apply() for immediate write
            }
            startTokenExpirationTimer(visitorResponse.expToken)
        }
    }

    fun hasValidToken(): Boolean {
        val token = getToken()
        val expiration = prefs.getLong(KEY_TOKEN_EXPIRATION, 0)
        return !token.isNullOrEmpty() && expiration > System.currentTimeMillis()
    }

    fun getToken(): String? {
        try {
            val token = prefs.getString(KEY_TOKEN, null)
            Log.d("TokenManager", "Retrieving token...")
            Log.d("TokenManager", "Current prefs content: ${prefs.all}")
            Log.d("TokenManager", "Retrieved token: $token")
            return token
        } catch (e: Exception) {
            Log.e("TokenManager", "Error retrieving token", e)
            return null
        }
    }

    //Obtener el id del usuario
    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }

    private fun verifyPreferencesFile() {
        try {
            val prefsFile = File(appContext.applicationInfo.dataDir + "/shared_prefs/" + PREFS_TOKEN_FILE + ".xml")
            Log.d("TokenManager", "Preferences file exists: ${prefsFile.exists()}")

            if (prefsFile.exists()) {
                Log.d("TokenManager", "Preferences file size: ${prefsFile.length()} bytes")
                Log.d("TokenManager", "Preferences file path: ${prefsFile.absolutePath}")
                Log.d("TokenManager", "Current preferences content: ${prefs.all}")
            } else {
                Log.e("TokenManager", "Preferences file does not exist!")
            }
        } catch (e: Exception) {
            Log.e("TokenManager", "Error verifying preferences file", e)
        }
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
                    // Add logging to track timer
                    Log.d("TokenManager", "Token expires in: ${millisUntilFinished / 1000} seconds")

                    if (millisUntilFinished <= REFRESH_THRESHOLD && isAppInForeground) {
                        refreshTokenCallback?.invoke()
                    }
                }

                override fun onFinish() {
                    Log.d("TokenManager", "Token expired, clearing data")
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
        Log.d("TokenManager", "Clearing auth data...")
        editor.clear()
        editor.commit()
        cancelExpirationTimer()
        tokenExpirationTimer?.cancel()
        tokenExpirationTimer = null

        // Verify clear operation
        Log.d("TokenManager", "Auth data cleared. Current prefs content: ${prefs.all}")
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
        private const val KEY_USER_ROLE = "user_role"
        private const val ROLE_VISITOR = "Visitante"
        private const val KEY_TOKEN_EXPIRATION = "token_expiration"
        private const val BACKGROUND_EXPIRATION_TIME = 3600000L //1 hora en milisegundos
        private const val REFRESH_THRESHOLD = 300000L //5 minutos
    }
}