package com.cotiledon.mobilApp.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.activities.BaseActivity
import com.cotiledon.mobilApp.ui.activities.MainActivity
import com.cotiledon.mobilApp.ui.backend.authentication.RetrofitAuthClient
import com.cotiledon.mobilApp.ui.managers.TokenManager
import kotlinx.coroutines.launch

class SignInFragment : Fragment() {

    private var emailEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var signInButton: Button? = null
    private var registerButton: TextView? = null
    private lateinit var tokenManager: TokenManager
    private lateinit var authClient: RetrofitAuthClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signin, container, false)
    }

    //Acceso al tokenManager para manejo de vida del token en la app
    override fun onAttach(context: Context) {
        super.onAttach(context)
        tokenManager = (requireActivity() as BaseActivity).tokenManager
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            initializeViews(view)
            setupClickListeners()

            authClient = RetrofitAuthClient.createAuthClient()

            if (tokenManager.getToken() != null) {
                navigateToMainActivity()
            }
        } catch (e: Exception) {
            Log.e("SignInFragment", "Error in onViewCreated: ${e.message}")
            Toast.makeText(context, "Error initializing login screen", Toast.LENGTH_LONG).show()
        }
    }

    private fun initializeViews(view: View) {
        emailEditText = view.findViewById(R.id.editText_singin_email)
        if (emailEditText == null) {
            throw IllegalStateException("Email EditText not found in layout")
        }

        passwordEditText = view.findViewById(R.id.editText_singin_password)
        if (passwordEditText == null) {
            throw IllegalStateException("Password EditText not found in layout")
        }

        signInButton = view.findViewById(R.id.button_singin)
        if (signInButton == null) {
            throw IllegalStateException("Sign in button not found in layout")
        }
        registerButton = view.findViewById(R.id.register_btn)
    }

    private fun setupClickListeners() {
        signInButton?.setOnClickListener {
            validateAndLogin()
        }

        registerButton?.setOnClickListener {
            navigateToRegister()
        }
    }

    private fun validateAndLogin() {
        val email = emailEditText?.text?.toString()?.trim() ?: ""
        val password = passwordEditText?.text?.toString()?.trim() ?: ""

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = authClient.login(email, password)

                when (response.code()) {
                    201 -> {
                        val authResponse = response.body()
                        if (authResponse != null) {
                            Log.d("SignInFragment", "Auth Response: $authResponse")  // Add logging
                            tokenManager.saveAuthData(
                                authResponse.access_token,  // Use access_token instead of accessToken
                                authResponse.id,
                                authResponse.expToken
                            )
                            Log.d("SignInFragment", "Token saved successfully")  // Add logging
                            navigateToMainActivity()
                        } else {
                            Log.e("SignInFragment", "Response body is null")
                            Toast.makeText(context, "Invalid response from server", Toast.LENGTH_SHORT).show()
                        }
                    }
                    401 -> {
                        Toast.makeText(context, "Invalid credentials", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Log.e("SignInFragment", "Unexpected response code: ${response.code()}")
                        Toast.makeText(context, "Login error", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("SignInFragment", "Login error", e)
                Toast.makeText(context, "Connection error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun navigateToRegister() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, UserRegisterFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        emailEditText = null
        passwordEditText = null
        signInButton = null
    }

}