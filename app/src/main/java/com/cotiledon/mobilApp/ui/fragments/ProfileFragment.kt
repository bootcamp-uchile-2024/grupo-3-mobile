package com.cotiledon.mobilApp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cotiledon.mobilApp.R

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment's layout - make sure to create fragment_profile.xml
        return inflater.inflate(R.layout.fragment_profile,container, false)
    }

    //TODO: Lógica de manejo de perfil de usuario
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}