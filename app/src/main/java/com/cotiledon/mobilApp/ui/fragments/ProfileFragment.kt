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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Since we're in a Fragment, we need to handle UI setup after the view is created
        /*setupUserProfile(view)
        setupProfileOptions(view)
        setupAccountSettings(view)*/
    }

    /*private fun setupUserProfile(view: View) {
        // Initialize profile header elements like user image, name, email
        val userProfileImage = view.findViewById<ImageView>(R.id.profile_image)
        val userName = view.findViewById<TextView>(R.id.profile_name)
        val userEmail = view.findViewById<TextView>(R.id.profile_email)

        userProfileImage.setOnClickListener {
            // Handle profile image click - maybe allow user to change their photo
        }

        // You might want to load user data from your data source
        loadUserData()
    }

    private fun setupProfileOptions(view: View) {
        // Set up various profile options like orders, wishlist, addresses
        val ordersOption = view.findViewById<LinearLayout>(R.id.orders_option)
        val wishlistOption = view.findViewById<LinearLayout>(R.id.wishlist_option)
        val addressesOption = view.findViewById<LinearLayout>(R.id.addresses_option)

        ordersOption.setOnClickListener {
            // Instead of starting a new activity, we can navigate to another fragment
            navigateToOrders()
        }

        wishlistOption.setOnClickListener {
            navigateToWishlist()
        }

        addressesOption.setOnClickListener {
            navigateToAddresses()
        }
    }

    private fun setupAccountSettings(view: View) {
        // Set up account-related settings like notifications, privacy, etc.
        val notificationSettings = view.findViewById<LinearLayout>(R.id.notification_settings)
        val privacySettings = view.findViewById<LinearLayout>(R.id.privacy_settings)
        val logoutButton = view.findViewById<Button>(R.id.logout_button)

        notificationSettings.setOnClickListener {
            navigateToNotificationSettings()
        }

        privacySettings.setOnClickListener {
            navigateToPrivacySettings()
        }

        logoutButton.setOnClickListener {
            handleLogout()
        }
    }

    private fun loadUserData() {
        // Load user data from your data source (e.g., SharedPreferences, API, etc.)
        // You might want to use a ViewModel for this in a more complete implementation
    }

    private fun navigateToOrders() {
        // Use Fragment navigation to go to orders screen
        // You might want to use the Navigation component or Fragment transactions
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, OrdersFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToWishlist() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, WishlistFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToAddresses() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, AddressesFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToNotificationSettings() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, NotificationSettingsFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToPrivacySettings() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PrivacySettingsFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun handleLogout() {
        // Handle logout logic
        // For example:
        // 1. Clear user session
        // 2. Clear any stored user data
        // 3. Navigate back to login screen
    }*/
}