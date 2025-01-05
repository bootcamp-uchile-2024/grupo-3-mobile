package com.cotiledon.mobilApp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.backend.user.RetrofitUserClient
import com.cotiledon.mobilApp.ui.dataClasses.profile.ProfileResponse
import com.cotiledon.mobilApp.ui.managers.TokenManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class ProfileUserFragment : Fragment() {
    private lateinit var greetingsText: TextView
    private lateinit var usernameText: TextView
    private lateinit var accountInfoLayout: LinearLayout
    private lateinit var addressInfoLayout: LinearLayout
    private lateinit var couponsLayout: LinearLayout
    private lateinit var notificationsLayout: LinearLayout
    private lateinit var purchaseHistoryLayout: LinearLayout
    private lateinit var logoutLayout: LinearLayout
    private lateinit var tokenManager: TokenManager
    private lateinit var userClient: RetrofitUserClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_user_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tokenManager = TokenManager(requireContext())
        userClient = RetrofitUserClient.createUserClient(TokenManager(requireContext()))

        initializeViews(view)
        //setupClickListeners()
        loadUserProfile()
    }

    private fun initializeViews(view: View) {
        greetingsText = view.findViewById(R.id.TextView_hello)
        usernameText = view.findViewById(R.id.TextView_username)

        // Initialize clickable layouts
        /*accountInfoLayout = view.findViewById(R.id.account_info_layout)
        addressInfoLayout = view.findViewById(R.id.address_info_layout)
        couponsLayout = view.findViewById(R.id.coupons_layout)
        notificationsLayout = view.findViewById(R.id.notifications_layout)
        purchaseHistoryLayout = view.findViewById(R.id.purchase_history_layout)
        logoutLayout = view.findViewById(R.id.logout_layout)*/
    }

    /*private fun setupClickListeners() {
        accountInfoLayout.setOnClickListener {
            // TODO: Navigate to account info screen
        }

        addressInfoLayout.setOnClickListener {
            // TODO: Navigate to addresses screen
        }

        couponsLayout.setOnClickListener {
            // TODO: Navigate to coupons screen
        }

        notificationsLayout.setOnClickListener {
            // TODO: Navigate to notifications screen
        }

        purchaseHistoryLayout.setOnClickListener {
            // TODO: Navigate to purchase history screen
        }

        logoutLayout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }*/

    private fun loadUserProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = userClient.getUserProfile()

                if (response.isSuccessful) {
                    response.body()?.let { profile ->
                        updateUI(profile)
                    }
                } else {
                    handleError("Error cargando perfil")
                }
            } catch (e: Exception) {
                handleError("Error de conexión")
            }
        }
    }

    private fun updateUI(profile: ProfileResponse) {
        greetingsText.text = getString(R.string.profile_greetings)
        usernameText.text = profile.nombreUsuario
    }

    private fun handleError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro que deseas cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                logout()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun logout() {
        tokenManager.clearAuthData()
        navigateToLogin()
    }

    private fun navigateToLogin() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SignInPreviousFragment())
            .commit()
    }

    companion object {
        fun newInstance() = ProfileUserFragment()
    }
}