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
import androidx.fragment.app.Fragment
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
import com.cotiledon.mobilApp.ui.managers.TokenManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch


class CatalogFragment : Fragment(), PlantFiltersBottomSheet.FilterListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlantRecyclerViewAdapter
    private lateinit var cartManager: CartStorageManager
    private lateinit var backButton: ImageView
    private lateinit var filterButton: Button
    private lateinit var sortButton: MaterialButton

    private var currentPage = 1
    private val pageSize = 10
    private var isLoading = false
    private var hasMoreItems = true

    //Guardado de filtros a nivel del fragment
    private var currentFilters: PlantFilterParams? = null
    private var currentSortParams: PlantFilterParams? = null
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
        //TODO: Implementar funcionalidad de búsqueda de productos
        //setupSearch()

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

        recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
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

    //TODO: Función para manejo de busqueda (asociar al backend, no debe aplicar filtro local necesariamente)
    /*
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
    }*/

    override fun onFiltersApplied(filterParams: PlantFilterParams) {
        // Apply new filters while preserving sorting
        filterParams.apply {
            orderBy = currentFilters?.orderBy
            order = currentFilters?.order
        }

        currentFilters = filterParams
        adapter.clearPlants()
        currentPage = 1
        isLoading = false
        hasMoreItems = true
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

    private fun loadPlants() {
        if (isLoading || !hasMoreItems) return
        isLoading = true
        adapter.showLoading()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val retrofitClient = RetrofitCatalogClient.createCatalogClient()
                val response: PlantResponse<Plant> = retrofitClient.getPlants(
                    page = currentPage,
                    pageSize = pageSize,
                    filterParams = currentFilters
                )

                hasMoreItems = currentPage * pageSize < response.totalItems

                if (response.data.isNotEmpty()) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        adapter.hideLoading()
                        adapter.updatePlants(response.data)
                        currentPage++
                    }
                }

            } catch (e: Exception) {
                Log.e("CatalogFragment", "Error loading plants", e)
                viewLifecycleOwner.lifecycleScope.launch {
                    adapter.hideLoading()
                    showRetryToast()
                }
            } finally {
                isLoading = false
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
    }
}