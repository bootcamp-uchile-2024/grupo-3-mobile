package com.cotiledon.mobilApp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.activities.MainContainerActivity
import com.cotiledon.mobilApp.ui.managers.CartStorageManager
import com.cotiledon.mobilApp.ui.managers.OrderManager
import com.cotiledon.mobilApp.ui.managers.TokenManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class ShoppingCartGratitudeFragment : Fragment() {
    private lateinit var continueShoppingButton: Button
    private lateinit var backButton: ImageButton

    private val tokenManager: TokenManager by lazy {
        TokenManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shopping_cart_gratitude, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        continueShoppingButton = view.findViewById(R.id.button_continue)
        setupContinueShoppingButton()

        backButton = view.findViewById(R.id.btn_back)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupContinueShoppingButton() {
        continueShoppingButton.setOnClickListener {

            if (tokenManager.isVisitor()) {
                // If they are a visitor, clear their credentials
                handleVisitorLogout()
            }

            (activity as? MainContainerActivity)?.let { mainActivity ->
                val bottomNav = mainActivity.findViewById<BottomNavigationView>(R.id.bottom_navigation)

                //Limpiar el backstack completo
                parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

                //Ir al fragmento de Home
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment())
                    .commit()

                //Actualizar el item del nav_menu
                bottomNav.selectedItemId = R.id.nav_home
            }
        }
    }

    private fun handleVisitorLogout() {
        try {
            // Clear the token manager credentials
            tokenManager.clearAuthData()

            // Clear any stored visitor details in OrderManager
            OrderManager.clearVisitorDetails()

            // Clear any stored cart data
            val cartManager = CartStorageManager(requireContext(), tokenManager)
            cartManager.clearCart()

            // Since the user profile might have been created during checkout,
            // we should ensure it's cleared from any local storage
            context?.getSharedPreferences("visitor_prefs", Context.MODE_PRIVATE)
                ?.edit()
                ?.clear()
                ?.apply()

        } catch (e: Exception) {
            Log.e("ShoppingCartGratitude", "Error clearing visitor credentials", e)
            // Even if there's an error, we'll continue with navigation
            // since this is the end of the flow
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.popBackStack()
                }
            }
        )
    }

    companion object {
        fun newInstance() = ShoppingCartGratitudeFragment()
    }
}