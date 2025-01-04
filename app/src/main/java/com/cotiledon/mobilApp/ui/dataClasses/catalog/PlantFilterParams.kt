package com.cotiledon.mobilApp.ui.dataClasses.catalog


data class PlantFilterParams(
    var environment: Int? = null,
    var petFriendly: Boolean? = null,
    var rating: Int? = null,
    var maxPrice: Int? = null,
    var minPrice: Int? = null,
    var temperatureTolerance: Int? = null,
    var lighting: Int? = null,
    var irrigationType: Int? = null,
    var orderBy: String? = null,
    var order: String? = null,
    var size: String? = null
) {
    companion object {
        const val ORDER_ASC = "ASC"
        const val ORDER_DESC = "DESC"

        const val ORDER_BY_RATING = "puntuacion"
        const val ORDER_BY_PRICE = "precio"
        const val ORDER_BY_UNITS_SOLD = "unidadesVendidas"

        const val SIZE_S = "S"
        const val SIZE_M = "M"
        const val SIZE_L = "L"
        const val SIZE_XL = "XL"
    }
}