package com.cotiledon.mobilApp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.activities.MainContainerActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ShoppingCartGratitudeFragment : Fragment() {
    private lateinit var continueShoppingButton: Button

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
    }

    private fun setupContinueShoppingButton() {
        continueShoppingButton.setOnClickListener {

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

    companion object {
        fun newInstance() = ShoppingCartGratitudeFragment()
    }
}