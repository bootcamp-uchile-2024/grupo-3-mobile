package com.cotiledon.mobilApp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.activities.UserRegistrationActivity

class SignInPreviousFragment : Fragment() {

    private lateinit var registerBtn: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signin_previous, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerBtn = view.findViewById(R.id.signin_register_btn)

        registerBtn.setOnClickListener {
            val intent = Intent(requireContext(), UserRegistrationActivity::class.java)
            startActivity(intent)
        }
    }
}




