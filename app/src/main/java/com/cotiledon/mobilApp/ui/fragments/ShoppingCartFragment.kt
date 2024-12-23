package com.cotiledon.mobilApp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.adapters.CartRecyclerViewAdapter
import com.cotiledon.mobilApp.ui.managers.CartStorageManager
import java.text.NumberFormat
import java.util.Locale

class ShoppingCartFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartManager: CartStorageManager
    private lateinit var adapter: CartRecyclerViewAdapter
    private lateinit var totalPriceText: TextView
    private lateinit var checkoutButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shopping_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartManager = CartStorageManager(requireContext())

        recyclerView = view.findViewById(R.id.shopping_cart_recycler_view)
        totalPriceText = view.findViewById(R.id.total_price)
        checkoutButton = view.findViewById(R.id.btn_ir_a_pagar)

        setupRecyclerView()
        setupCheckoutButton()
        updateCartUI()
        updateProductPrice()
    }

    private fun formatPrice(amount: Double): String {
        // Create a formatter for Chilean peso
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        return formatter.format(amount)
    }

    private fun updateProductPrice() {
        // Get all items from cart manager and calculate total
        val cartItems = cartManager.loadCartItems()
        val totalProductPrice = cartItems.sumOf { item ->
            item.plantPrice.toDouble() * item.plantQuantity
        }

        // Update UI with formatted price
        view?.findViewById<TextView>(R.id.product_price)?.text = formatPrice(totalProductPrice)

        // Update product count in text
        val totalItems = cartManager.cartItemsCount()
        view?.findViewById<TextView>(R.id.product_price_text)?.text =
            "Costo de tus productos ($totalItems)"

        // After updating product price, always update total
        updateTotalPrice()
    }

    private fun updateTotalPrice() {
        try {
            // Get the components
            val productPriceText = view?.findViewById<TextView>(R.id.product_price)?.text.toString()
            val discountText = view?.findViewById<TextView>(R.id.discount_value)?.text.toString()
            val shippingText = view?.findViewById<TextView>(R.id.shippment_cost)?.text.toString()

            // Convert string prices to doubles (removing currency symbol and dots)
            val productPrice = convertPriceToDouble(productPriceText)
            val discount = convertPriceToDouble(discountText)
            val shipping = convertPriceToDouble(shippingText)

            // Calculate total
            val totalPrice = productPrice - discount + shipping

            // Update UI
            view?.findViewById<TextView>(R.id.total_price)?.text = formatPrice(totalPrice)
        } catch (e: Exception) {
            Log.e("ShoppingCartFragment", "Error calculating total price", e)
        }
    }

    private fun convertPriceToDouble(priceString: String): Double {
        return try {
            // Remove currency symbol, dots, and spaces, then parse
            priceString.replace("$", "")
                .replace(".", "")
                .trim()
                .toDouble()
        } catch (e: Exception) {
            0.0
        }
    }

    private fun setupRecyclerView() {
        adapter = CartRecyclerViewAdapter(
            cartPlants = cartManager.loadCartItems().toMutableList(),
            cartStorageManager = cartManager,
            onItemRemoved = {
                updateCartUI()
                updateProductPrice()
            }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = this@ShoppingCartFragment.adapter
        }
    }

    private fun setupCheckoutButton() {
        checkoutButton.setOnClickListener {
            val orderSummaryFragment = ShoppingCartOrderSummaryFragment1()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, orderSummaryFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateCartUI() {
        // Update recycler view
        adapter.notifyDataSetChanged()

        // Update total price
        val totalPrice = cartManager.getTotalCartPrice()
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        totalPriceText.text = formatter.format(totalPrice)

        // Enable/disable checkout button based on cart contents
        checkoutButton.isEnabled = cartManager.loadCartItems().isNotEmpty()
    }

    companion object {
        fun newInstance() = ShoppingCartFragment()
    }
}