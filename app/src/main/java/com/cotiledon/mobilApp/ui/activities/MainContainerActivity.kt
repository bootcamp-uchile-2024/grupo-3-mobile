package com.cotiledon.mobilApp.ui.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.fragments.CategoriesFragment
import com.cotiledon.mobilApp.ui.fragments.HomeFragment
import com.cotiledon.mobilApp.ui.fragments.ProfileFragment
import com.cotiledon.mobilApp.ui.fragments.ShoppingCartFragment
import com.cotiledon.mobilApp.ui.fragments.SignInPreviousFragment
import com.cotiledon.mobilApp.ui.managers.CartStorageManager
import com.cotiledon.mobilApp.ui.managers.TokenManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainContainerActivity : BaseActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var cartManager: CartStorageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_container)
        supportActionBar?.hide()

        //Inicialiar el cart manager
        cartManager = CartStorageManager(this, TokenManager(this))

        //Inicializar el botom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        //Inicializar el cart badge
        updateCartBadge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Inicializar siempre la vista con el HomeFragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }
        //Manejo de la navegación
        setupNavigation()
    }

    private fun setupNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_profile -> SignInPreviousFragment()
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

     fun updateCartBadge() {
        val itemCount = cartManager.cartItemsCount()
        val cartItem = bottomNavigationView.menu.findItem(R.id.nav_cart)

        if (itemCount > 0) {
            val badge = bottomNavigationView.getOrCreateBadge(cartItem.itemId)
            badge.apply {
                number = itemCount
                isVisible = true
                backgroundColor = ContextCompat.getColor(this@MainContainerActivity, R.color.badge_red)
            }
        } else {
            bottomNavigationView.removeBadge(cartItem.itemId)
        }
    }

}