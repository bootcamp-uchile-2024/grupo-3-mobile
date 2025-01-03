package com.cotiledon.mobilApp.ui.menus

import com.cotiledon.mobilApp.ui.dataClasses.category.PlantFilters
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

//TODO: Implementar menu de filtros (bottom sheet menu)
class PlantFiltersBottomSheet : BottomSheetDialogFragment() {

    //Para comunicar los filtros a la vista de catálogo
    interface FilterListener {
        fun onFiltersApplied(filters: PlantFilters)
    }

    //TODO: Completar la clase -> Bottom Sheet de filtros al clickear el boton de Filtro en la vista de catálogo
}