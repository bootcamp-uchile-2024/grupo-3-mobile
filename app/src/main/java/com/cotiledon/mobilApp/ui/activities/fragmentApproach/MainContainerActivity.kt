package com.cotiledon.mobilApp.ui.activities.fragmentApproach

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.fragments.CategoriesFragment
import com.cotiledon.mobilApp.ui.fragments.HomeFragment
import com.cotiledon.mobilApp.ui.fragments.ProfileFragment
import com.cotiledon.mobilApp.ui.fragments.ShoppingCartFragment
import com.cotiledon.mobilApp.ui.managers.CartStorageManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainContainerActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var cartManager: CartStorageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_container)
        supportActionBar?.hide()

        // Initialize cart manager
        cartManager = CartStorageManager(this)

        // Initialize bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Set up cart badge initially
        setupCartBadge()

        // Handle window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize with HomeFragment if this is the first creation
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        setupNavigation()
    }

    private fun setupNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_profile -> ProfileFragment()
                R.id.nav_cart -> ShoppingCartFragment()
                R.id.nav_menu -> CategoriesFragment()
                else -> null
            }

            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
                true
            } ?: false
        }
    }

    // Method to set up the cart badge
    private fun setupCartBadge() {
        val cartItem = bottomNavigationView.menu.findItem(R.id.nav_cart)
        val cartItemCount = cartManager.loadCartItems().size

        if (cartItemCount > 0) {
            // Get or create the badge
            var badge = bottomNavigationView.getBadge(R.id.nav_cart)
            if (badge == null) {
                badge = bottomNavigationView.getOrCreateBadge(R.id.nav_cart)
            }

            // Configure the badge
            badge.apply {
                backgroundColor = getColor(R.color.badge_red) // Use your app's color
                badgeTextColor = getColor(R.color.white)
                maxCharacterCount = 3
                number = cartItemCount
                isVisible = true
            }
        } else {
            // Remove the badge if cart is empty
            bottomNavigationView.removeBadge(R.id.nav_cart)
        }
    }

    fun updateCartBadge() {
        val cartManager = CartStorageManager(this)
        val cartItemCount = cartManager.loadCartItems().size

        val cartItem = bottomNavigationView.menu.findItem(R.id.nav_cart)

        if (cartItemCount > 0) {
            var badge = bottomNavigationView.getBadge(R.id.nav_cart)
            if (badge == null) {
                badge = bottomNavigationView.getOrCreateBadge(R.id.nav_cart)
            }

            badge.apply {
                number = cartItemCount
                isVisible = true
            }
        } else {
            bottomNavigationView.removeBadge(R.id.nav_cart)
        }
    }

    // Optional: Method to update badge with a specific count
    fun updateCartBadgeWithCount(count: Int) {
        val badge = bottomNavigationView.getOrCreateBadge(R.id.nav_cart)
        if (count > 0) {
            badge.apply {
                number = count
                isVisible = true
            }
        } else {
            bottomNavigationView.removeBadge(R.id.nav_cart)
        }
    }

}