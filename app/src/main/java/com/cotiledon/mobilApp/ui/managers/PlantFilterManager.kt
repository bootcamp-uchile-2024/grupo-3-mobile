package com.cotiledon.mobilApp.ui.managers

import androidx.room.util.copy
import com.cotiledon.mobilApp.ui.dataClasses.PlantFilters
import com.cotiledon.mobilApp.ui.enums.GrowthHabit
import com.cotiledon.mobilApp.ui.enums.IrrigationType
import com.cotiledon.mobilApp.ui.enums.Lighting
import com.cotiledon.mobilApp.ui.enums.PlantCycle
//TODO: Implementar manager de filtros
class PlantFilterManager {
    private var currentFilters = PlantFilters()

    fun updatePriceRange(min: Float, max: Float) {
        currentFilters = currentFilters.copy(priceRange = min..max)
    }

    fun updateHeightRange(min: Float, max: Float) {
        currentFilters = currentFilters.copy(heightRange = min..max)
    }

    fun updateTemperatureRange(min: Float, max: Float) {
        currentFilters = currentFilters.copy(temperatureRange = min..max)
    }

    fun updateCycle(cycle: PlantCycle?) {
        currentFilters = currentFilters.copy(cycle = cycle)
    }

    fun updateLighting(lighting: Lighting?) {
        currentFilters = currentFilters.copy(lighting = lighting)
    }

    fun updateFloral(hasFlowers: Boolean?) {
        currentFilters = currentFilters.copy(hasFlowers = hasFlowers)
    }

    fun updateIrrigationType(type: IrrigationType?) {
        currentFilters = currentFilters.copy(irrigationType = type)
    }

    fun updatePetFriendly(isPetFriendly: Boolean?) {
        currentFilters = currentFilters.copy(isPetFriendly = isPetFriendly)
    }

    fun updateGrowthHabit(habit: GrowthHabit?) {
        currentFilters = currentFilters.copy(growthHabit = habit)
    }

    fun resetFilters() {
        currentFilters = PlantFilters()
    }

    fun getFilters() = currentFilters
}