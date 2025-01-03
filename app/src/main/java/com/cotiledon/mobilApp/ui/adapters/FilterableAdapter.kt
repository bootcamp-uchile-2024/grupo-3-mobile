package com.cotiledon.mobilApp.ui.adapters

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.ui.dataClasses.category.PlantFilters

abstract class FilterableAdapter <T: Any, VH: RecyclerView.ViewHolder>(
    private var items: List<T>): RecyclerView.Adapter<VH>()
{
 protected var currentFilters : PlantFilters = PlantFilters()
 private var filteredItems: List<T> = items

    @SuppressLint("NotifyDataSetChanged")
    open fun filter(filters: PlantFilters){

        currentFilters = filters

        filteredItems = items.filter {item ->
            true
        }
        notifyDataSetChanged()
    }

    fun updateItems(newItems: List<T>) {
        items = newItems
        filter(currentFilters)
    }

    override fun getItemCount(): Int = filteredItems.size
}