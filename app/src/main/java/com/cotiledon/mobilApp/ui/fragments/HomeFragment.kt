package com.cotiledon.mobilApp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.activities.MainContainerActivity
import com.cotiledon.mobilApp.ui.adapters.PlantRecyclerViewAdapter
import com.cotiledon.mobilApp.ui.backend.catalog.RetrofitCatalogClient
import com.cotiledon.mobilApp.ui.dataClasses.cart.CartPlant
import com.cotiledon.mobilApp.ui.dataClasses.catalog.PlantFilterParams
import com.cotiledon.mobilApp.ui.dataClasses.plant.Plant
import com.cotiledon.mobilApp.ui.managers.CartStorageManager
import com.cotiledon.mobilApp.ui.managers.TokenManager
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlantRecyclerViewAdapter
    private lateinit var cartManager: CartStorageManager
    private val catalogClient = RetrofitCatalogClient.createCatalogClient()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupSearchBar(view)
        initializeViews(view)
        setupRecyclerView()
        loadTopRatedProducts()
        setupCartManager()
        setupCategories(view)
        setupBanners(view)
        setupBottomButtons(view)
    }

    override fun onPause() {
        super.onPause()
        adapter.clearPlants()
    }

    private fun setupSearchBar(view: View) {
        val searchEditText = view.findViewById<EditText>(R.id.search_edit_text)
        val cameraButton = view.findViewById<ImageView>(R.id.camera_button)

        searchEditText.setOnClickListener {
        }

        cameraButton.setOnClickListener {
        }
    }

    //TODO: Manejo de selección de categoría
    private fun setupCategories(view: View) {
        val categoryAll = view.findViewById<TextView>(R.id.category_all)
        val categoryHighlighted = view.findViewById<TextView>(R.id.category_highlited)
        val categoryAccessories = view.findViewById<TextView>(R.id.category_accesories)
        val categorySupplies = view.findViewById<TextView>(R.id.category_supplies)

        categoryAll.setOnClickListener {
        }

        categoryHighlighted.setOnClickListener {
        }

        categoryAccessories.setOnClickListener {
        }

        categorySupplies.setOnClickListener {
        }
    }

    //TODO: Manejo de lógica de banners
    private fun setupBanners(view: View) {
        val upperBanner = view.findViewById<ImageView>(R.id.imageView_banner_up)
        val lowerBanner = view.findViewById<ImageView>(R.id.activity_main_banner_down)

        upperBanner.setOnClickListener {
        }

        lowerBanner.setOnClickListener {
        }
    }

    //TODO: Manejo de botones inferiores
    private fun setupBottomButtons(view: View) {
        val plantAiButton = view.findViewById<Button>(R.id.app_plant_ai_button)
        val benefitsButton = view.findViewById<Button>(R.id.benefits_button)
        val tipsButton = view.findViewById<Button>(R.id.tips_button)

        plantAiButton.setOnClickListener {
        }

        benefitsButton.setOnClickListener {
        }

        tipsButton.setOnClickListener {
        }
    }

    private fun setupCartManager() {
        val tokenManager = TokenManager(requireContext())
        cartManager = CartStorageManager(requireContext(), tokenManager)
    }

    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.activity_main_recyclerview)
    }

    private fun setupRecyclerView() {
        adapter = PlantRecyclerViewAdapter(
            initialPlants = mutableListOf(),
            onItemClick = { plant -> navigateToProductDetail(plant) },
            onAddToCartClick = { plant -> handleAddToCart(plant) }
        )

        recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            this.adapter = this@HomeFragment.adapter
        }
    }

    private fun loadTopRatedProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val filterParams = PlantFilterParams(
                    orderBy = PlantFilterParams.ORDER_BY_RATING,
                    order = PlantFilterParams.ORDER_DESC
                )

                val response = catalogClient.getPlants(
                    page = 1,
                    pageSize = 6,
                    filterParams = filterParams
                )

                if (response.data.isNotEmpty()) {
                    adapter.updatePlants(response.data)
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error cargando los productos recomendados.", e)
            }
        }
    }

    private fun handleAddToCart(plant: Plant) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                if (plant.stock > 0) {
                    val cartPlant = plant.imagenes.firstOrNull()?.ruta?.let {
                        CartPlant(
                            plantName = plant.nombre,
                            plantPrice = plant.precio.toString(),
                            plantId = plant.id.toString(),
                            plantStock = plant.stock.toString(),
                            plantQuantity = 1,
                            plantImage = it.drop(1)
                        )
                    }

                    cartPlant?.let {
                        cartManager.saveProductToCart(it)
                        Toast.makeText(
                            requireContext(),
                            "${plant.nombre} agregado al carrito.",
                            Toast.LENGTH_SHORT
                        ).show()
                        (activity as? MainContainerActivity)?.updateCartBadge()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Lo sentimos, ${plant.nombre} ya no está disponible.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error cargando el producto al carrito, intentelo nuevamente.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun navigateToProductDetail(plant: Plant) {
        val productDetailFragment = ProductDetailFragment.newInstance(plant)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, productDetailFragment)
            .addToBackStack(null)
            .commit()
    }
}