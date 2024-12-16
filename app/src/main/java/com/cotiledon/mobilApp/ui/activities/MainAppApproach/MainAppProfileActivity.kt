package com.cotiledon.mobilApp.ui.activities.MainAppApproach

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cotiledon.mobilApp.R
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView

/*class MainAppProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_app)
        supportActionBar?.hide()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    val intent = Intent(this, MainAppHomeActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_profile -> {
                    true
                }

                R.id.nav_cart -> {
                    val intent = Intent(this, MainAppShoppingCarActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_menu -> {
                    val intent = Intent(this, MainAppCategoriesActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

    }

}*/
