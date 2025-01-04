package com.cotiledon.mobilApp.ui.adapters

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.ui.dataClasses.catalog.PlantFilterParams

abstract class FilterableAdapter<T: Any, VH: RecyclerView.ViewHolder>(
    private var items: List<T>
): RecyclerView.Adapter<VH>() {
    protected var currentFilters: PlantFilterParams = PlantFilterParams()
    var filteredItems: List<T> = items

    @SuppressLint("NotifyDataSetChanged")
    open fun filter(filters: PlantFilterParams) {

        currentFilters = filters


        filteredItems = items.filter { item ->
            true
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearItems() {
        items = emptyList()
        filteredItems = emptyList()
        notifyDataSetChanged()
    }

    fun updateItems(newItems: List<T>) {
        items = newItems
        filter(currentFilters)
    }

    override fun getItemCount(): Int = filteredItems.size
}