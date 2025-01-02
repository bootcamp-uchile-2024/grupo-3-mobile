package com.cotiledon.mobilApp.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.activities.MainContainerActivity
import com.cotiledon.mobilApp.ui.adapters.PlantRecyclerViewAdapter
import com.cotiledon.mobilApp.ui.dataClasses.CartPlant
import com.cotiledon.mobilApp.ui.dataClasses.Plant
import com.cotiledon.mobilApp.ui.dataClasses.PlantFilters
import com.cotiledon.mobilApp.ui.dataClasses.PlantResponse
import com.cotiledon.mobilApp.ui.managers.CartStorageManager
import com.cotiledon.mobilApp.ui.retrofit.GlobalValues
import com.cotiledon.mobilApp.ui.retrofit.RetrofitCatalogClient
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class CatalogFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlantRecyclerViewAdapter
    private lateinit var cartManager: CartStorageManager
    private lateinit var backButton: ImageView
    private lateinit var filterButton: Button
    private lateinit var sortButton: Button

    private var currentPage = 1
    private val pageSize = 10
    private var isLoading = false
    private var hasMoreItems = true

    //Guardado de filtros a nivel del fragment
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
        // Inicializar el Cart Manager
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

        //TODO: Establecer funcionalidad para el botón de filtro (crear bottom drawer para manejo de filtros)
        /*filterButton.setOnClickListener {
            showFilterDialog()
        }*/

        //TODO: Establecer funcionalidad para el botón de ordenamiento
        /*
        sortButton.setOnClickListener {
            showSortDialog()
        }*/
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

    //TODO: Función para aplicar filtros seleccionados en el Bottom Drawer
    /*private fun filterPlants(searchQuery: String) {
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
    }*/

    //TODO: Función para mostrar el bottom drawer de filtros
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

    //TODO: Refinar función para muestra de interfaz de ordenamiento y valores dentro de ella (utiliar un Spinner quizás para la selección de opciones)
    private fun showSortDialog() {
        // TODO: Implementar opciones con un array de strin para evitar hardcoding
        //val options = arrayOf("Price: Low to High", "Price: High to Low", "Name: A to Z", "Name: Z to A")

        //TODO: Implementar funcionalidad de ordenamiento con Spinner en vez de AlertDialog
        /*
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
            .show()*/
    }

    //TODO: Integrar carga de datos con filtro de categoría
    private fun loadPlants() {
        if (isLoading || !hasMoreItems) return
        isLoading = true
        adapter.showLoading()

        val categoryId = arguments?.getString("category_id")?.toIntOrNull()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val retrofitClient = RetrofitCatalogClient.createCatalogClient()
                val response: PlantResponse<Plant> = retrofitClient.getPlants(
                    page = currentPage,
                    pageSize = pageSize
                )

                hasMoreItems = currentPage * pageSize < response.totalItems

                val newPlants = response.data.filter { plant ->
                    categoryId == null || plant.idCategoria == categoryId
                }

                if (newPlants.isNotEmpty()) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        adapter.hideLoading()
                        adapter.updatePlants(newPlants)
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

                if (cartPlant != null) {
                    cartManager.saveProductToCart(cartPlant)
                }

                Toast.makeText(
                    requireContext(),
                    "${plant.nombre} añadido al carrito",
                    Toast.LENGTH_SHORT
                ).show()

                (activity as? MainContainerActivity)?.updateCartBadge()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Lo sentimos, ${plant.nombre} no tiene más stock",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Log.e("CatalogFragment", "Error al añadir producto al carrito", e)
            Toast.makeText(
                requireContext(),
                "No se pudo añadir el producto al carrito",
                Toast.LENGTH_SHORT
            ).show()
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