package com.cotiledon.mobilApp.ui.fragments

import com.cotiledon.mobilApp.ui.menus.PlantFiltersBottomSheet
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.activities.MainContainerActivity
import com.cotiledon.mobilApp.ui.adapters.PlantRecyclerViewAdapter
import com.cotiledon.mobilApp.ui.dataClasses.cart.CartPlant
import com.cotiledon.mobilApp.ui.dataClasses.plant.Plant
import com.cotiledon.mobilApp.ui.dataClasses.plant.PlantResponse
import com.cotiledon.mobilApp.ui.managers.CartStorageManager
import com.cotiledon.mobilApp.ui.backend.catalog.RetrofitCatalogClient
import com.cotiledon.mobilApp.ui.dataClasses.catalog.PlantFilterParams
import com.cotiledon.mobilApp.ui.fragments.base.SearchableFragment
import com.cotiledon.mobilApp.ui.helpers.SearchBarHelper
import com.cotiledon.mobilApp.ui.managers.TokenManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException
import retrofit2.HttpException


class CatalogFragment : SearchableFragment(), PlantFiltersBottomSheet.FilterListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlantRecyclerViewAdapter
    private lateinit var cartManager: CartStorageManager
    private lateinit var backButton: ImageView
    private lateinit var filterButton: Button
    private lateinit var sortButton: MaterialButton
    private lateinit var searchBarHelper: SearchBarHelper

    private var currentPage = 1
    private val pageSize = 10
    private var isLoading = false
    private var hasMoreItems = true
    private var currentSearchQuery: String = ""
    private var isSearchMode = false
    // Add a job to track and cancel ongoing searches
    private var searchJob: Job? = null

    //Guardado de filtros a nivel del fragment
    private var currentFilters: PlantFilterParams? = null
    private var currentSortParams: PlantFilterParams? = null
    private var currentPlants = mutableListOf<Plant>()

    private var popupWindow: PopupWindow? = null

    // Add this method to show the AI explanation popup
    private fun showAIExplanationPopup(explanation: String) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.ai_explanation_popup, null)

        // Initialize the popup window
        popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            elevation = 16f
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        // Set the explanation text
        popupView.findViewById<TextView>(R.id.ai_explanation_text).text = explanation

        // Set up dismiss button
        popupView.findViewById<Button>(R.id.dismiss_button).setOnClickListener {
            popupWindow?.dismiss()
        }

        // Show the popup centered in the screen
        val rootView = requireActivity().window.decorView.findViewById<View>(android.R.id.content)
        popupWindow?.showAtLocation(rootView, Gravity.CENTER, 0, 0)

        // Add dim background
        val dimBackground = ColorDrawable(Color.BLACK)
        dimBackground.alpha = 120 // Value between 0-255
        popupWindow?.setBackgroundDrawable(dimBackground)
    }

    private sealed class DisplayMode {
        data object Catalog : DisplayMode()
        data class Search(val query: String) : DisplayMode()
    }
    private var currentMode: DisplayMode = DisplayMode.Catalog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_catalog, container, false)

        // Construct PlantFilterParams from individual arguments
        arguments?.let { args ->
            if (args.containsKey("environment")) {  // Check if we have AI-provided filters
                currentFilters = PlantFilterParams(
                    environment = args.getInt("environment"),
                    petFriendly = args.getBoolean("pet_friendly"),
                    lighting = args.getInt("lighting"),
                    temperatureTolerance = args.getInt("temperature"),
                    irrigationType = args.getInt("irrigation"),
                    size = args.getString("size")

                )

                // Show AI explanation if available
                args.getString("ai_explanation")?.let { explanation ->
                    view.post {
                        showAIExplanationPopup(explanation)
                    }
                }
            }
        }

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupRecyclerView()
        setupClickListeners()
        setupSearch()

    }

    override fun onResume() {
        super.onResume()
        resetLoadingState()
        loadPlants()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun resetLoadingState() {
        currentPage = 1
        isLoading = false
        hasMoreItems = true
        currentPlants.clear()
        adapter.notifyDataSetChanged()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val tokenManager = TokenManager(requireContext())

        cartManager = CartStorageManager(
            context = requireContext(),
            tokenManager
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel any ongoing operations
        searchJob?.cancel()
        popupWindow?.dismiss()
        popupWindow = null
    }

    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.rv_products)
        backButton = view.findViewById(R.id.btn_back)
        filterButton = view.findViewById(R.id.btn_filter)
        sortButton = view.findViewById(R.id.btn_sort)
    }


    private fun setupRecyclerView() {
        adapter = PlantRecyclerViewAdapter(
            initialPlants = mutableListOf(),
            onItemClick = {   plant -> navigateToProductDetail(plant)},
            onAddToCartClick = { plant -> handleAddToCart(plant) }
        )

        val gridLayoutManager = GridLayoutManager(context, 2).apply {
            // Tell the layout manager to span the loading indicator across all columns
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (adapter.getItemViewType(position)) {
                        PlantRecyclerViewAdapter.VIEW_TYPE_LOADING -> 2  // Full width for loading
                        else -> 1  // Normal width for items
                    }
                }
            }
        }

        recyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = this@CatalogFragment.adapter

            //Scroll listener para paginación
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (!isLoading && hasMoreItems) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                        ) {
                            loadPlants()
                        }
                    }
                }
            })
        }
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        filterButton.setOnClickListener {
            showFilterBottomSheet()
        }

        sortButton.setOnClickListener {
            showSortPopupMenu()
            animateIconRotation(sortButton, R.drawable.sort_icon_up)
        }
    }

    @SuppressLint("Recycle", "ObjectAnimatorBinding")
    private fun animateIconRotation(button: MaterialButton, newIconResId: Int) {
        val currentIcon = button.icon

        val rotateOut = ObjectAnimator.ofFloat(currentIcon, "rotationZ",0f, 180f)
        rotateOut.duration = 100

        rotateOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                val newIcon : Drawable? = ContextCompat.getDrawable(button.context, newIconResId)
                button.icon = newIcon
            }
        })

        rotateOut.start()
    }

    private fun setupSearch() {
        // Find the root view that contains the search bar components
        // We'll use the searchbar_section from your layout which is included in the catalog fragment
        val searchBarView = view?.findViewById<View>(R.id.searchbar_section)

        // Only initialize if we successfully found the search bar view
        searchBarView?.let { rootView ->
            // Create new SearchBarHelper instance with:
            // - rootView: The view containing the search components
            // - this: The fragment implementing SearchCallback interface
            searchBarHelper = SearchBarHelper(rootView, this)

            // Set an appropriate hint for the search
            searchBarHelper.setHint(getString(R.string.search_hint))
        }
    }

    // Override search-related methods from SearchableFragment
    override fun getSearchHint(): String {
        return getString(R.string.search_catalog_hint)
    }

    // Modify the search callbacks to handle state transitions
    override fun onQueryTextSubmit(query: String) {
        if (query.isNotEmpty()) {
            // Switch to search mode and refresh the display
            currentMode = DisplayMode.Search(query)
            refreshDisplay()
        }
    }

    override fun onQueryTextChange(newText: String) {
        // Cancel any ongoing search
        searchJob?.cancel()

        if (newText.isEmpty()) {
            // Reset to catalog mode with current filters
            currentMode = DisplayMode.Catalog

            // Clear existing plants
            adapter.clearPlants()

            // Reset pagination
            currentPage = 1
            isLoading = false
            hasMoreItems = true

            // Reload plants with current filters
            loadPlants()
        } else {
            // Start new search
            searchJob = lifecycleScope.launch(Dispatchers.Main) {
                try {
                    delay(300) // Debounce delay
                    currentMode = DisplayMode.Search(newText)

                    // Clear existing plants
                    adapter.clearPlants()

                    // Reset pagination
                    currentPage = 1
                    isLoading = false
                    hasMoreItems = true

                    // Load plants with search query
                    loadPlants()
                } catch (e: CancellationException) {
                    // Search was cancelled, do nothing
                }
            }
        }
    }

    // New method to handle display refresh
    private fun refreshDisplay() {
        // Cancel any ongoing search
        searchJob?.cancel()

        lifecycleScope.launch(Dispatchers.Main) {
            try {
                // Clear existing data first
                adapter.clearPlants()
                currentPage = 1
                isLoading = false
                hasMoreItems = true

                // Then load new data
                loadPlants()
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }



    override fun onFiltersApplied(filterParams: PlantFilterParams) {
        // Store the new filters
        currentFilters = filterParams

        // Switch back to catalog mode if we were in search mode
        currentMode = DisplayMode.Catalog

        // Clear existing plants and reset adapter
        adapter.clearPlants()

        // Reset pagination
        currentPage = 1
        isLoading = false
        hasMoreItems = true

        // Load plants with new filters
        loadPlants()
    }

    override fun getCurrentFilters(): PlantFilterParams? {
        return currentFilters
    }

    private fun showFilterBottomSheet() {
        val location = IntArray(2)
        filterButton.getLocationOnScreen(location)
        val filterButtonY = location[1] + filterButton.height

        val bottomSheet = PlantFiltersBottomSheet.newInstance(filterButtonY)

        val maxPrice = currentPlants.maxOfOrNull { it.precio.toFloat() } ?: 100000f
        bottomSheet.setMaxProductPrice(maxPrice)

        // Set the target fragment to ensure the listener connection
        bottomSheet.setTargetFragment(this, 0)
        bottomSheet.show(parentFragmentManager, "filters")


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun applyFilters(newFilters: PlantFilterParams) {
        currentPage = 1
        isLoading = false
        hasMoreItems = true

        currentPlants.clear()
        adapter.notifyDataSetChanged()

        currentFilters = newFilters

        loadPlants()
    }

    @SuppressLint("InflateParams")
    private fun showSortPopupMenu() {
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.sort_popup_menu, null)

        val sortPopupWindow = PopupWindow(
            popupView,
            205.dpToPx(requireContext()),
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            elevation = 16f
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            setOnDismissListener {
                animateIconRotation(sortButton, R.drawable.sort_icon)
                dismiss()
            }
        }

        popupView.apply {
            findViewById<TextView>(R.id.popular_products_option).setOnClickListener {
                applySorting(PlantFilterParams.ORDER_BY_RATING, PlantFilterParams.ORDER_DESC)
                sortPopupWindow.dismiss()
            }

            findViewById<TextView>(R.id.most_sold_option).setOnClickListener {
                applySorting(PlantFilterParams.ORDER_BY_UNITS_SOLD, PlantFilterParams.ORDER_DESC)
                sortPopupWindow.dismiss()
            }

            findViewById<TextView>(R.id.price_high_to_low_option).setOnClickListener {
                applySorting(PlantFilterParams.ORDER_BY_PRICE, PlantFilterParams.ORDER_DESC)
                sortPopupWindow.dismiss()
            }

            findViewById<TextView>(R.id.price_low_to_high_option).setOnClickListener {
                applySorting(PlantFilterParams.ORDER_BY_PRICE, PlantFilterParams.ORDER_ASC)
                sortPopupWindow.dismiss()
            }
        }

        val location = IntArray(2)
        sortButton.getLocationOnScreen(location)

        sortPopupWindow.showAsDropDown(
            sortButton,
            -65,
            0,
            Gravity.START
        )

    }


    @SuppressLint("NotifyDataSetChanged")
    private fun applySorting(orderBy: String, order: String) {
        val newParams = currentFilters?.copy() ?: PlantFilterParams()
        newParams.apply {
            this.orderBy = orderBy
            this.order = order
        }

        //Resetear y recargar con nuevos parámetros
        currentFilters = newParams

        adapter.clearPlants()

        currentPage = 1
        isLoading = false
        hasMoreItems = true

        //Cargar nueva data con los nuevos parámetros
        loadPlants()
    }



    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    // Modify loadPlants to use the new state management
    private fun loadPlants() {
        if (isLoading || !hasMoreItems) return
        isLoading = true
        adapter.showLoading()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val retrofitClient = RetrofitCatalogClient.createCatalogClient()
                val response: PlantResponse<Plant> = when (val mode = currentMode) {
                    is DisplayMode.Search -> {
                        // For search mode, we don't apply filters
                        retrofitClient.searchPlants(
                            page = currentPage,
                            pageSize = pageSize,
                            searchQuery = mode.query
                        )
                    }
                    DisplayMode.Catalog -> {
                        // For catalog mode, we apply current filters
                        retrofitClient.getPlants(
                            page = currentPage,
                            pageSize = pageSize,
                            filterParams = currentFilters
                        )
                    }
                }

                withContext(Dispatchers.Main) {
                    hasMoreItems = currentPage * pageSize < response.totalItems

                    if (response.data.isNotEmpty()) {
                        adapter.hideLoading()
                        adapter.updatePlants(response.data)
                        currentPage++
                    } else {
                        showEmptyState(currentMode)
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    handleError(e)
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    adapter.hideLoading()
                }
            }
        }
    }

    private fun handleError(error: Exception) {
        Log.e(TAG, "Error loading plants", error)

        activity?.runOnUiThread {
            // Hide loading state
            adapter.hideLoading()

            // Show error message
            val errorMessage = when (error) {
                is IOException -> getString(R.string.error_network)
                is HttpException -> getString(R.string.error_server)
                else -> getString(R.string.error_loading_plants)
            }

            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    // Helper method to show appropriate empty state
    private fun showEmptyState(mode: DisplayMode) {
        val message = when (mode) {
            is DisplayMode.Search -> getString(R.string.no_search_results, mode.query)
            DisplayMode.Catalog -> getString(R.string.no_catalog_items)
        }

        activity?.runOnUiThread {
            // Hide loading state first
            adapter.hideLoading()

            // Show empty state message
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
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

                        activity?.runOnUiThread {
                            Toast.makeText(
                                requireContext(),
                                "${plant.nombre} añadido al carrito",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        (activity as? MainContainerActivity)?.updateCartBadge()
                    }

                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Lo sentimos, ${plant.nombre} no tiene más stock",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                Log.e("CatalogFragment", "Error al añadir producto al carrito", e)
                activity?.runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "No se pudo añadir el producto al carrito",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showRetryToast() {
        Toast.makeText(
            requireContext(),
            "Error al cargar las plantas. Por favor, intente nuevamente.",
            Toast.LENGTH_LONG
        ).show()
    }

    //Metodo para checkear si un producto ya está en el carrito o no
    private fun isPlantInCart(plantId: String): Boolean {
        return cartManager.loadCartItems().any { it.plantId == plantId }
    }

    //Metodo para obtener la cantidad de un producto en el carrito
    private fun getPlantQuantityInCart(plantId: String): Int {
        return cartManager.loadCartItems()
            .find { it.plantId == plantId }
            ?.plantQuantity ?: 0
    }

    //Función para navegar a la vista de detalle
    private fun navigateToProductDetail(plant: Plant) {

        val productDetailFragment = ProductDetailFragment.newInstance(plant)

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, productDetailFragment)
            .addToBackStack(null)
            .commit()
    }

    //Fragment factory
    companion object {
        fun newInstance(args: Bundle) : CatalogFragment {
            return CatalogFragment().apply {
                arguments = args
            }
        }
        fun createArguments(categoryId: String): Bundle {
            return Bundle().apply {
                putString("category_id", categoryId)
            }
        }
        private const val TAG = "CatalogFragment"
    }
}