package com.cotiledon.mobilApp.ui.dataClasses

import com.cotiledon.mobilApp.ui.enums.GrowthHabit
import com.cotiledon.mobilApp.ui.enums.IrrigationType
import com.cotiledon.mobilApp.ui.enums.Lighting
import com.cotiledon.mobilApp.ui.enums.PlantCycle

data class PlantFilters (
    var priceRange: ClosedFloatingPointRange<Float> = 0f..10000000f,
    var heightRange: ClosedFloatingPointRange<Float> = 0f..50000f,
    var cycle: PlantCycle? = null,
    var lighting: Lighting? = null,
    var hasFlowers: Boolean? = null,
    var temperatureRange: ClosedFloatingPointRange<Float> = 0f..50f,
    var irrigationType: IrrigationType? = null,
    var isPetFriendly: Boolean? = null,
    var growthHabit: GrowthHabit? = null
)