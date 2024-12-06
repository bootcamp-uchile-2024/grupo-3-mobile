package com.cotiledon.mobilApp.ui.fragments

import com.cotiledon.mobilApp.ui.dataClasses.PlantFilters
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PlantFiltersBottomSheet : BottomSheetDialogFragment() {

    //Para comunicar los filtros a la vista de catálogo
    interface FilterListener {
        fun onFiltersApplied(filters: PlantFilters)
    }

    //TODO: Completar la clase -> Bottom Sheet de filtros al clickear el boton de Filtro en la vista de catálogo
}