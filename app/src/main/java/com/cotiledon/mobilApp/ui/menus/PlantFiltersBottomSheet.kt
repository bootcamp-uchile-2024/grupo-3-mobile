package com.cotiledon.mobilApp.ui.menus

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioGroup
import android.widget.TextView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.dataClasses.catalog.PlantFilterParams
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.RangeSlider
import java.text.NumberFormat
import java.util.Locale

class PlantFiltersBottomSheet : BottomSheetDialogFragment() {

    // Interface to communicate filter changes
    interface FilterListener {
        fun onFiltersApplied(filterParams: PlantFilterParams)
        fun getCurrentFilters(): PlantFilterParams?
    }

    private var maxProductPrice: Float = 100000f  // Default max value if no products
    private val minAllowedPrice: Float = 0f      // Minimum value always 0
    private val minMaxPrice: Float = 1000f       // Minimum allowed maximum value

    private var filterListener: FilterListener? = null
    private lateinit var currentFilters: PlantFilterParams

    // Views that we'll need to interact with
    private lateinit var budgetSlider: RangeSlider
    private lateinit var budgetValueText: TextView
    private lateinit var sizeGroup: RadioGroup
    private lateinit var petFriendlyCheckbox: CheckBox
    private lateinit var lightGroup: RadioGroup
    private lateinit var temperatureGroup: RadioGroup
    private lateinit var irrigationGroup: RadioGroup
    private lateinit var clearButton: Button
    private lateinit var applyButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate our custom layout for the bottom sheet
        return inflater.inflate(R.layout.filter_bottom_sheet_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize all our views
        initializeViews(view)

        // Set up the bottom sheet behavior
        setupBottomSheetBehavior()

        // If we have existing filters, initialize the UI with them
        // Initialize filters from parent
        currentFilters = filterListener?.getCurrentFilters()?.copy() ?: PlantFilterParams()
        initializeFilterValues()
        setupFilterListeners()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Try multiple approaches to find the listener
        filterListener = when {
            // First, check if the parent fragment implements the interface
            parentFragment is FilterListener -> parentFragment as FilterListener
            // If not, check if the containing activity implements it
            context is FilterListener -> context
            // If neither, check if there's a target fragment that implements it
            targetFragment is FilterListener -> targetFragment as FilterListener
            else -> {
                // If we still haven't found a listener, let's try one more time with the parent fragment manager
                parentFragmentManager.fragments.firstOrNull { it is FilterListener } as? FilterListener
                    ?: throw RuntimeException("Either parent fragment, target fragment, or activity must implement FilterListener")
            }
        }
    }

    private fun initializeViews(view: View) {
        // Find all our views using findViewById
        budgetSlider = view.findViewById(R.id.budgetSlider)
        budgetValueText = view.findViewById(R.id.budgetValue)
        sizeGroup = view.findViewById(R.id.sizeGroup)
        petFriendlyCheckbox = view.findViewById(R.id.petFriendlyCheckbox)
        lightGroup = view.findViewById(R.id.lightGroup)
        temperatureGroup = view.findViewById(R.id.temperatureGroup)
        irrigationGroup = view.findViewById(R.id.irrigationGroup)
        clearButton = view.findViewById(R.id.clearFiltersButton)
        applyButton = view.findViewById(R.id.applyFilterButton)

        setupBudgetSlider()
    }

    private fun setupBudgetSlider() {
        budgetSlider.apply {
            // Set the value range
            valueFrom = minAllowedPrice
            valueTo = maxOf(maxProductPrice, minMaxPrice)

            // Set initial values - start at 0 and max at the higher value
            values = listOf(minAllowedPrice, valueTo)

            // Update the text display
            updateBudgetText(minAllowedPrice, valueTo)

            // Set step size - optional, depends on your needs
            stepSize = 1000f  // Steps of 1000 pesos
        }
    }

    private fun setupBottomSheetBehavior() {
        // Get the BottomSheetDialog and customize its behavior
        (dialog as? BottomSheetDialog)?.let { bottomSheetDialog ->
            bottomSheetDialog.behavior.apply {
                // Calculate where the bottom sheet should appear
                val filterButtonBottom = requireArguments().getInt(ARG_FILTER_BUTTON_Y, 0)
                val screenHeight = resources.displayMetrics.heightPixels
                val peekHeight = screenHeight - filterButtonBottom

                // Set up the behavior properties
                setPeekHeight(peekHeight)
                maxHeight = peekHeight
                isDraggable = false
                state = BottomSheetBehavior.STATE_EXPANDED

                // Add a callback to handle state changes
                addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        // Ensure the sheet stays at the correct height
                        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                            state = BottomSheetBehavior.STATE_EXPANDED
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        // Not needed for our implementation
                    }
                })
            }
        }
    }

    fun setMaxProductPrice(price: Float) {
        maxProductPrice = maxOf(price, minMaxPrice)  // Ensure it's at least 1000
        // Update the slider if it's initialized
        if (::budgetSlider.isInitialized) {
            setupBudgetSlider()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Create a customized BottomSheetDialog
        return BottomSheetDialog(requireContext(), theme).apply {
            // Customize the window properties
            window?.apply {
                // Add a semi-transparent background
                setDimAmount(0.3f)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
    }

    override fun getTheme(): Int {
        // Use our custom theme for the bottom sheet
        return R.style.BottomSheetDialogTheme
    }

    private fun setupFilterListeners() {
        budgetSlider.addOnChangeListener { slider, _, _ ->
            val values = slider.values

            // First value (min) is always 0
            val minValue = minAllowedPrice

            // Ensure max value is at least minMaxPrice
            val maxValue = maxOf(values[1], minMaxPrice)

            // Update the filter values
            currentFilters.apply {
                minPrice = minValue.toInt()
                maxPrice = maxValue.toInt()
            }

            // Update the display text
            updateBudgetText(minValue, maxValue)

            // Force the values to maintain our rules
            if (values[0] != minValue || values[1] != maxValue) {
                slider.values = listOf(minValue, maxValue)
            }
        }

        // Size radio group listener
        sizeGroup.setOnCheckedChangeListener { _, checkedId ->
            currentFilters.size = when (checkedId) {
                R.id.sizeS -> PlantFilterParams.SIZE_S
                R.id.sizeM -> PlantFilterParams.SIZE_M
                R.id.sizeL -> PlantFilterParams.SIZE_L
                R.id.sizeXL -> PlantFilterParams.SIZE_XL
                else -> null
            }
        }

        // Pet friendly checkbox listener
        petFriendlyCheckbox.setOnCheckedChangeListener { _, isChecked ->
            currentFilters.petFriendly = if (isChecked) true else null
        }

        // Light group listener - assuming backend uses integers 1-3 for lighting types
        lightGroup.setOnCheckedChangeListener { _, checkedId ->
            currentFilters.lighting = when (checkedId) {
                R.id.lightDirect -> 1  // Sol Directo
                R.id.lightPartial -> 2 // Semi Sombra
                R.id.lightShade -> 3   // Sombra
                else -> null
            }
        }

        // Temperature tolerance listener - assuming backend uses integers 1-3 for temperature types
        temperatureGroup.setOnCheckedChangeListener { _, checkedId ->
            currentFilters.temperatureTolerance = when (checkedId) {
                R.id.tempWarm -> 1    // Cálido
                R.id.tempMild -> 2    // Templado
                R.id.tempCold -> 3    // Frío
                else -> null
            }
        }

        // Irrigation type listener - assuming backend uses integers 1-7 for irrigation types
        irrigationGroup.setOnCheckedChangeListener { _, checkedId ->
            currentFilters.irrigationType = when (checkedId) {
                R.id.irrigationManual -> 1      // Manual
                R.id.irrigationDrip -> 2        // Goteo
                R.id.irrigationCapillary -> 3   // Capilar
                R.id.irrigationSubmersion -> 4  // Sumersión
                R.id.irrigationSelfWatering -> 5// Autorriego
                R.id.irrigationMisting -> 6     // Nebulización
                R.id.irrigationAutomatic -> 7   // Automático
                else -> null
            }
        }

        // Clear filters button
        clearButton.setOnClickListener {
            resetFilters()
        }
    }

    private fun initializeFilterValues() {
        // Set initial values based on current filters
        currentFilters.apply {
            // Set budget slider
            minPrice?.let { min ->
                maxPrice?.let { max ->
                    budgetSlider.values = listOf(min.toFloat(), max.toFloat())
                    updateBudgetText(min.toFloat(), max.toFloat())
                }
            }

            // Set size radio button
            when (size) {
                PlantFilterParams.SIZE_S -> sizeGroup.check(R.id.sizeS)
                PlantFilterParams.SIZE_M -> sizeGroup.check(R.id.sizeM)
                PlantFilterParams.SIZE_L -> sizeGroup.check(R.id.sizeL)
                PlantFilterParams.SIZE_XL -> sizeGroup.check(R.id.sizeXL)
            }

            // Set pet friendly checkbox
            petFriendlyCheckbox.isChecked = petFriendly == true

            // Set lighting radio button
            lighting?.let {
                val lightId = when (it) {
                    1 -> R.id.lightDirect
                    2 -> R.id.lightPartial
                    3 -> R.id.lightShade
                    else -> null
                }
                lightId?.let { id -> lightGroup.check(id) }
            }

            // Set temperature radio button
            temperatureTolerance?.let {
                val tempId = when (it) {
                    1 -> R.id.tempWarm
                    2 -> R.id.tempMild
                    3 -> R.id.tempCold
                    else -> null
                }
                tempId?.let { id -> temperatureGroup.check(id) }
            }

            // Set irrigation radio button
            irrigationType?.let {
                val irrigationId = when (it) {
                    1 -> R.id.irrigationManual
                    2 -> R.id.irrigationDrip
                    3 -> R.id.irrigationCapillary
                    4 -> R.id.irrigationSubmersion
                    5 -> R.id.irrigationSelfWatering
                    6 -> R.id.irrigationMisting
                    7 -> R.id.irrigationAutomatic
                    else -> null
                }
                irrigationId?.let { id -> irrigationGroup.check(id) }
            }
        }
    }

    private fun resetFilters() {
        budgetSlider.values = listOf(minAllowedPrice, maxOf(maxProductPrice, minMaxPrice))
        sizeGroup.clearCheck()
        petFriendlyCheckbox.isChecked = false
        lightGroup.clearCheck()
        temperatureGroup.clearCheck()
        irrigationGroup.clearCheck()

        // Reset filter params
        currentFilters = PlantFilterParams()
    }

    @SuppressLint("SetTextI18n")
    private fun updateBudgetText(min: Float, max: Float) {
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        budgetValueText.text = "${formatter.format(min.toDouble())} - ${formatter.format(max.toDouble())}"
    }

    companion object {
        private const val ARG_FILTER_BUTTON_Y = "filter_button_y"

        fun newInstance(filterButtonY: Int): PlantFiltersBottomSheet {
            return PlantFiltersBottomSheet().apply {
                arguments = Bundle().apply {
                    putInt(ARG_FILTER_BUTTON_Y, filterButtonY)
                }
            }
        }
    }
}