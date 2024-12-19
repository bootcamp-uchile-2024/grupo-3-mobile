package com.cotiledon.mobilApp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
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
    }

    private fun setupRecyclerView() {
        adapter = CartRecyclerViewAdapter(
            cartPlants = cartManager.loadCartItems().toMutableList(),
            cartStorageManager = cartManager,
            onItemRemoved = { updateCartUI() }
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