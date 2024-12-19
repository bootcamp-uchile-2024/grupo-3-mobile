package com.cotiledon.mobilApp.ui.activities.mainAppApproach

/*class MainAppCategoriesActivity : AppCompatActivity() {
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
                    val intent = Intent(this, MainAppHomeActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_profile -> {
                    val intent = Intent(this, MainAppProfileActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_cart -> {
                    val intent = Intent(this, MainAppShoppingCarActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_menu -> {
                    // Redirige a la actividad asociada al icono "Home"
                    true
                }

                else -> false
            }
        }

    }

}*/
