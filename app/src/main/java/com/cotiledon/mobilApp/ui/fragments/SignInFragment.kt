package com.cotiledon.mobilApp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.activities.BaseActivity
import com.cotiledon.mobilApp.ui.activities.MainContainerActivity
import com.cotiledon.mobilApp.ui.backend.authentication.RetrofitAuthClient
import com.cotiledon.mobilApp.ui.managers.TokenManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class SignInFragment : Fragment() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signInButton: Button
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

        initializeViews(view)
        setupClickListeners()

        tokenManager = TokenManager(requireContext())
        authClient = RetrofitAuthClient.createAuthClient()

        if (tokenManager.getToken() != null) {
            navigateToMain()
        }
    }

    private fun initializeViews(view: View){
        emailEditText = view.findViewById(R.id.edit_text_email)
        passwordEditText = view.findViewById(R.id.editText_singin_password)
        signInButton = view.findViewById(R.id.button_singin)
    }

    private fun setupClickListeners() {
        signInButton.setOnClickListener {
            validateAndLogin()
        }
    }

    private fun validateAndLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            try {
                val response = authClient.login(email, password)

                when (response.code()) {
                    201 -> {
                        response.body()?.let { authResponse ->
                            //Gynere el token
                            tokenManager.saveToken(authResponse.accessToken)
                            //Navegamos al Home
                            navigateToMain()
                        }
                    }
                    401 -> {
                        Toast.makeText(
                            context,
                            "Credenciales inválidas",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        Toast.makeText(
                            context,
                            "Error al iniciar sesión",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Error de conexión",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun navigateToMain() {
        (activity as? MainContainerActivity)?.let { mainActivity ->
            val bottomNav = mainActivity.findViewById<BottomNavigationView>(R.id.bottom_navigation)

            parentFragmentManager.popBackStack()

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()

            bottomNav.selectedItemId = R.id.nav_home
        }
    }
}