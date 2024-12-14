package com.cotiledon.mobilApp.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.activities.FragmentApproach.MainContainerActivity
import com.cotiledon.mobilApp.ui.activities.MainActivity
import com.cotiledon.mobilApp.ui.adapters.PlantRecyclerViewAdapter
import com.cotiledon.mobilApp.ui.dataClasses.CartPlant
import com.cotiledon.mobilApp.ui.dataClasses.Plant
import com.cotiledon.mobilApp.ui.dataClasses.PlantFilters
import com.cotiledon.mobilApp.ui.managers.CartStorageManager
import com.google.android.material.snackbar.Snackbar


class CatalogFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlantRecyclerViewAdapter
    private lateinit var cartManager: CartStorageManager
    private lateinit var backButton: ImageView
    private lateinit var filterButton: Button
    private lateinit var sortButton: Button

    // We'll store the current filters at the fragment level
    private var currentFilters = PlantFilters()
    private var currentPlants = mutableListOf<Plant>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_catalog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupRecyclerView()
        setupClickListeners()
        setupSearch()

        // Load initial data
        loadPlants()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Initialize the cart manager with the context
        cartManager = CartStorageManager(requireContext())
    }

    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.rv_products)
        backButton = view.findViewById(R.id.btn_back)
        filterButton = view.findViewById(R.id.btn_filter)
        sortButton = view.findViewById(R.id.btn_sort)
    }

    private fun setupRecyclerView() {
        adapter = PlantRecyclerViewAdapter(
            plants = mutableListOf(),
            onItemClick = { plant -> navigateToPlantDetail(plant) },
            onAddToCartClick = { plant -> handleAddToCart(plant) }
        )

        recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = this@CatalogFragment.adapter
        }
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        /*filterButton.setOnClickListener {
            showFilterDialog()
        }*/

        sortButton.setOnClickListener {
            showSortDialog()
        }
    }

    private fun setupSearch() {
        val searchEditText = view?.findViewById<EditText>(R.id.search_edit_text)

        searchEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // Apply search filter
                val searchQuery = s?.toString() ?: ""
                filterPlants(searchQuery)
                    }
        })
    }

    private fun filterPlants(searchQuery: String) {
        // First apply the search query
        val searchResults = if (searchQuery.isEmpty()) {
            currentPlants
        } else {
            currentPlants.filter { plant ->
                plant.nombre.contains(searchQuery, ignoreCase = true) ||
                        plant.descripcion.contains(searchQuery, ignoreCase = true)
            }
        }.toMutableList()

        // Then apply any other active filters
        adapter.updatePlants(searchResults)
        adapter.filter(currentFilters)
    }

    /*private fun showFilterDialog() {
        // Create and show your filter dialog
        // When filters are applied, update currentFilters and refresh the list
        val filterDialog = PlantFilterDialog(requireContext(), currentFilters)
        filterDialog.setOnFilterAppliedListener { filters ->
            currentFilters = filters
            adapter.filter(filters)
        }
        filterDialog.show()
    }*/

    private fun showSortDialog() {
        // Implement your sorting logic here
        val options = arrayOf("Price: Low to High", "Price: High to Low", "Name: A to Z", "Name: Z to A")

        AlertDialog.Builder(requireContext())
            .setTitle("Sort By")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> currentPlants.sortBy { it.precio }
                    1 -> currentPlants.sortByDescending { it.precio }
                    2 -> currentPlants.sortBy { it.nombre }
                    3 -> currentPlants.sortByDescending { it.nombre }
                }
                adapter.updatePlants(currentPlants)
            }
            .show()
    }

    private fun loadPlants() {
        // Show loading state
        adapter.showLoading()

        // Here you would typically make your API call or database query
        // For now, we'll simulate loading with some sample data
        val categoryId = arguments?.getString("category_id")

        // Replace this with your actual data fetching logic
        fetchPlantsForCategory(categoryId) { plants ->
            currentPlants.clear()
            currentPlants.addAll(plants)
            adapter.updatePlants(plants)
            adapter.hideLoading()
        }
    }

    private fun fetchPlantsForCategory(categoryId: String?, callback: (List<Plant>) -> Unit) {
        // Implement your actual data fetching logic here
        // This could be an API call, database query, etc.
        // For now, it's just a placeholder
    }

    private fun navigateToPlantDetail(plant: Plant) {
        // Navigate to your existing plant detail fragment
        val detailFragment = PlantDetailFragment.newInstance(plant.id)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, detailFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun handleAddToCart(plant: Plant) {
        try {
            if (plant.cantidad > 0) {
                // Convert and add to cart
                val cartPlant = CartPlant(
                    plantName = plant.nombre,
                    plantPrice = plant.precio.toString(),
                    plantId = plant.id.toString(),
                    plantStock = plant.cantidad.toString(),
                    plantQuantity = 1,
                    plantImage = plant.imagen ?: ""
                )

                cartManager.saveProductToCart(cartPlant)

                // Show success message
                Snackbar.make(
                    requireView(),
                    "${plant.nombre} added to cart",
                    Snackbar.LENGTH_SHORT
                ).show()

                // Update the cart badge
                (activity as? MainContainerActivity)?.updateCartBadge()
            } else {
                Snackbar.make(
                    requireView(),
                    "Sorry, ${plant.nombre} is out of stock",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Log.e("CatalogFragment", "Error adding item to cart", e)
            Snackbar.make(
                requireView(),
                "Couldn't add item to cart. Please try again.",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    // Helper method to check if a plant is already in cart
    private fun isPlantInCart(plantId: String): Boolean {
        return cartManager.loadCartItems().any { it.plantId == plantId }
    }

    // Helper method to get current quantity in cart for a plant
    private fun getPlantQuantityInCart(plantId: String): Int {
        return cartManager.loadCartItems()
            .find { it.plantId == plantId }
            ?.plantQuantity ?: 0
    }

    companion object {
        fun newInstance(categoryId: String) = CatalogFragment().apply {
            arguments = Bundle().apply {
                putString("category_id", categoryId)
            }
        }
    }
}