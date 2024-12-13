package com.cotiledon.mobilApp.ui.activities

import android.os.Bundle
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cotiledon.mobilApp.R
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainAppHomeActivity : AppCompatActivity() {
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

        // Configura el listener para manejar las selecciones
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Redirige a la actividad asociada al icono "Home"
                    if (this !is MainAppHomeActivity) {
                        val intent = Intent(this, MainAppHomeActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(0, 0)
                    }
                    true
                }

                R.id.nav_profile -> {
                    // Redirige a la actividad asociada al icono "Perfil"
                    if (false) {
                        val intent = Intent(this, PaymentConfirmationActivity::class.java)
                        startActivity(intent)
                    }
                    true
                }

                R.id.nav_cart -> {
                    // Redirige a la actividad asociada al icono "Carrito"
                    if (false) {
                        val intent = Intent(this, ProductActivity::class.java)
                        startActivity(intent)
                    }
                    true
                }

                R.id.nav_menu -> {
                    // Redirige a la actividad asociada al icono "Menú"
                    if (false) {
                        val intent = Intent(this, ShippingDetailsActivity::class.java)
                        startActivity(intent)
                    }
                    true
                }

                else -> false
            }
        }

    }

}
