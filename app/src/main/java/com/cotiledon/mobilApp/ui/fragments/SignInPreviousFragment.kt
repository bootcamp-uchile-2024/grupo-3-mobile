package com.cotiledon.mobilApp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.cotiledon.mobilApp.R


class SignInPreviousFragment : Fragment() {
    private lateinit var signInButton: Button
    private lateinit var registerButton: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signin_previous, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupClickListeners()
    }

    private fun initializeViews(view: View) {
        signInButton = view.findViewById(R.id.signin_btn)
        registerButton = view.findViewById(R.id.signin_register_btn)
    }

    private fun setupClickListeners() {
        signInButton.setOnClickListener {
            navigateToSignIn()
        }

        registerButton.setOnClickListener {
            navigateToRegister()
        }
    }

    private fun navigateToSignIn() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SignInFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToRegister() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, UserRegisterFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }
}




