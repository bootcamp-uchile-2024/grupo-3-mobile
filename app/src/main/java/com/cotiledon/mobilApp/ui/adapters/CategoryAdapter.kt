package com.cotiledon.mobilApp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.dataClasses.catalog.Category

class CategoryAdapter (
    private val categories: List<Category>,
    private val onClick: (Category) -> Unit
    )   : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.iv_subcategory)
        val name: TextView = itemView.findViewById(R.id.tv_subcategory_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.name.text = category.name
        holder.image.setImageResource(category.imageResId)
        holder.itemView.setOnClickListener { onClick(category) }
    }

    override fun getItemCount() = categories.size
}