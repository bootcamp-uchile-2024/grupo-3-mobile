package com.cotiledon.mobilApp.ui.adapters

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.ui.dataClasses.catalog.PlantFilterParams

abstract class FilterableAdapter<T: Any, VH: RecyclerView.ViewHolder>(
    initialItems: List<T>
): RecyclerView.Adapter<VH>() {
    private var items : MutableList<T> = initialItems.toMutableList()
    private var currentFilters: PlantFilterParams = PlantFilterParams()
    protected var filteredItems: MutableList<T> = items.toMutableList()

    @SuppressLint("NotifyDataSetChanged")
    open fun filter(filters: PlantFilterParams) {
        currentFilters = filters
        // We don't need to filter here anymore since filtering happens server-side
        // Just update the current filters
        notifyDataSetChanged()
    }


    @SuppressLint("NotifyDataSetChanged")
    fun clearItems() {
        items.clear()
        filteredItems.clear()
        notifyDataSetChanged()
    }

    // Modify updateItems to consider current filters
    fun updateItems(newItems: List<T>) {
        val startPosition = items.size
        items.addAll(newItems)
        // Since filtering happens server-side, filtered items are the same as new items
        filteredItems.addAll(newItems)
        notifyItemRangeInserted(startPosition, newItems.size)
    }

    override fun getItemCount(): Int = filteredItems.size
}