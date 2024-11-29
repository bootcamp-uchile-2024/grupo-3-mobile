package com.cotiledon.mobilApp.ui.asyncservices

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cotiledon.mobilApp.ui.dataClasses.UserRegistration
import com.cotiledon.mobilApp.ui.retrofit.RetrofitClient
import kotlinx.coroutines.launch

class RegistrationViewModel : ViewModel() {
    private val _registrationResult = MutableLiveData<Result<Unit>>()
    val registrationResult: LiveData<Result<Unit>> = _registrationResult

    fun registerUser(userRegistration: UserRegistration) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.userApiService.registerUser(userRegistration)
                if (response.isSuccessful) {
                    _registrationResult.postValue(Result.success(Unit))
                } else {
                    _registrationResult.postValue(Result.failure(
                        Exception("Error en el Registro de Usuario")
                    ))
                }
            } catch (e: Exception) {
                _registrationResult.postValue(Result.failure(e))
            }
        }
    }
}