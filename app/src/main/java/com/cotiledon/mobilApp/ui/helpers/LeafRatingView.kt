package com.cotiledon.mobilApp.ui.helpers

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import com.cotiledon.mobilApp.R
//Helper para crear vistas de rating con hojas en vez de estrellas
class LeafRatingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val leaves = Array(5) { ImageView(context) }

    init {
        orientation = HORIZONTAL
        leaves.forEach {
            addView(it, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ))
        }
    }

    fun setRating(rating: Float) {
        leaves.forEachIndexed { index, imageView ->
            if (index < rating) {
                imageView.setImageResource(R.drawable.leaf_filled)
            } else {
                imageView.setImageResource(R.drawable.leaf_empty)
            }
        }
    }
}