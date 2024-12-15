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

        // Carga de data inicial
        //TODO: Integrar carga de datos con el backend
        loadPlants()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Inicializar el Cart Manager con el contexto
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
        // Mostrar estado de carga
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

    //TODO: Crear función para comunicación con el backend para carga de datos de plantas
    private fun fetchPlantsForCategory(categoryId: String?, callback: (List<Plant>) -> Unit) {
        // Implement your actual data fetching logic here
        // This could be an API call, database query, etc.
        // For now, it's just a placeholder
    }

    //TODO: Función para navegación a detalle de planta
    private fun navigateToPlantDetail(plant: Plant) {
        // Navigate to your existing plant detail fragment
        val detailFragment = ProductDetailFragment.newInstance(plant.id)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, detailFragment)
            .addToBackStack(null)
            .commit()
    }

    //TODO: Función para manejo de agregar a carrito
    private fun handleAddToCart(plant: Plant) {
        try {
            //Comprobar que la cantidad de stock es mayor a 0 en el producto
            if (plant.cantidad > 0) {
                //Crear objeto para guardar en el carrito en caso positivo
                val cartPlant = CartPlant(
                    plantName = plant.nombre,
                    plantPrice = plant.precio.toString(),
                    plantId = plant.id.toString(),
                    plantStock = plant.cantidad.toString(),
                    plantQuantity = 1,
                    //TODO: Implementar manejo de imagenes en el carrito
                    plantImage = plant.imagen ?: ""
                )

                //Se guarda el producto en el carrito
                cartManager.saveProductToCart(cartPlant)

                //Muestra de mensaje de confirmación
                Snackbar.make(
                    requireView(),
                    "${plant.nombre} añadido al carrito",
                    Snackbar.LENGTH_SHORT
                ).show()

                //Actualizar el "badge" (globo rojo) del carrito en el nav.view para mostrar la cantidad de productos
                (activity as? MainContainerActivity)?.updateCartBadge()
            } else {
                //En caso de no tener stock, informar al usuario
                Snackbar.make(
                    requireView(),
                    "Lo sentimos, ${plant.nombre} no tiene más stock",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Log.e("CatalogFragment", "Error al añadir producto al carrito", e)
            Snackbar.make(
                requireView(),
                "No se pudo añadir el producto al carrito, por favor vuelva a intentarlo.",
                Snackbar.LENGTH_SHORT
            ).show()
        }
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

    //Función para inicializar el fragment de catálogo desde la vista de subcategoría, entregando el nombre de la categoría como Bundle.
    companion object {
        fun newInstance(categoryId: String) = CatalogFragment().apply {
            arguments = Bundle().apply {
                putString("category_id", categoryId)
            }
        }
    }
}