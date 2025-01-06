package com.cotiledon.mobilApp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.adapters.CartRecyclerViewAdapter
import com.cotiledon.mobilApp.ui.dataClasses.cart.CartProduct
import com.cotiledon.mobilApp.ui.dataClasses.cart.CartValidationErrorResponse

import com.cotiledon.mobilApp.ui.managers.CartStorageManager
import com.cotiledon.mobilApp.ui.backend.cart.RetrofitCartClient
import com.cotiledon.mobilApp.ui.managers.TokenManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class ShoppingCartFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CartRecyclerViewAdapter
    private lateinit var totalPriceText: TextView
    private lateinit var checkoutButton: Button
    private var checkoutTimer: CountDownTimer? = null


    private val tokenManager by lazy { TokenManager(requireContext()) }
    private val cartClient by lazy { RetrofitCartClient.createCartClient(tokenManager) }
    private val cartManager by lazy { CartStorageManager(requireContext(), tokenManager) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shopping_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.shopping_cart_recycler_view)
        totalPriceText = view.findViewById(R.id.total_price)
        checkoutButton = view.findViewById(R.id.btn_ir_a_pagar)


        recyclerView = view.findViewById(R.id.shopping_cart_recycler_view)
        totalPriceText = view.findViewById(R.id.total_price)
        checkoutButton = view.findViewById(R.id.btn_ir_a_pagar)

        setupRecyclerView()
        setupCheckoutButton()
        updateCartUI()
        updateProductPrice()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        checkoutTimer?.cancel()
    }


    private fun formatPrice(amount: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        return formatter.format(amount)
    }

    @SuppressLint("SetTextI18n")
    private fun updateProductPrice() {
        val cartItems = cartManager.loadCartItems()
        val totalProductPrice = cartItems.sumOf { item ->
            item.plantPrice.toDouble() * item.plantQuantity
        }


        view?.findViewById<TextView>(R.id.product_price)?.text = formatPrice(totalProductPrice)


        val totalItems = cartManager.cartItemsCount()
        view?.findViewById<TextView>(R.id.product_price_text)?.text =
            "Costo de tus productos ($totalItems)"


        updateTotalPrice()
    }

    private fun updateTotalPrice() {
        try {
            val productPriceText = view?.findViewById<TextView>(R.id.product_price)?.text.toString()
            val discountText = view?.findViewById<TextView>(R.id.discount_value)?.text.toString()
            val shippingText = view?.findViewById<TextView>(R.id.shippment_cost)?.text.toString()

            val productPrice = convertPriceToDouble(productPriceText)
            val discount = convertPriceToDouble(discountText)
            val shipping = convertPriceToDouble(shippingText)

            val totalPrice = productPrice - discount + shipping

            view?.findViewById<TextView>(R.id.total_price)?.text = formatPrice(totalPrice)
        } catch (e: Exception) {
            Log.e("ShoppingCartFragment", "Error calculating total price", e)
        }
    }

    private fun convertPriceToDouble(priceString: String): Double {
        return try {
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
            },
            scope = viewLifecycleOwner.lifecycleScope
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = this@ShoppingCartFragment.adapter
        }
    }

    private fun setupCheckoutButton() {
        checkoutButton.setOnClickListener {
            validateAndProceed()
        }
    }

    private fun validateAndProceed() {
        lifecycleScope.launch {
            try {
                val cartId = cartManager.getCartId()
                if (cartId != null) {
                    val cartProducts = cartManager.loadCartItems().map { item ->
                        CartProduct(
                            productoId = item.plantId.toInt(),
                            cantidadProducto = item.plantQuantity
                        )
                    }
                    Log.d("ShoppingCartFragment", "Cart Product: $cartProducts")

                    val response = cartClient.validateCartProducts(cartId, cartProducts)
                    Log.d("ShoppingCartFragment", "Response: $response")

                    when (response.code()) {
                        201 -> {
                            startCheckoutTimer()
                            navigateToNextStep()
                        }
                        400 -> {
                            val errorResponse = Gson().fromJson(
                                response.errorBody()?.string(),
                                CartValidationErrorResponse::class.java
                            )
                            Log.d("ShoppingCartFragment", "Error validating cart: $errorResponse")
                            showConflictDialog(errorResponse)
                        }
                        else -> {
                            Toast.makeText(
                                requireContext(),
                                "Error validando el carrito",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ShoppingCartFragment", "Error validating cart", e)
                Toast.makeText(
                    requireContext(),
                    "Error validando el carrito",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showConflictDialog(conflictResponse: CartValidationErrorResponse) {
        val conflicts = conflictResponse.response.productosCarro
        if (conflicts.isEmpty()) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Error de Stock")
                .setMessage("Hubo un problema validando el stock de los productos.")
                .setPositiveButton("Aceptar") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            return
        }

        val conflictMessages = conflicts.mapNotNull { conflict ->
            val product = cartManager.loadCartItems().find { it.plantId == conflict.productoId.toString() }
            product?.let {
                "${it.plantName}: Stock máximo disponible: ${conflict.cantidadProducto}"
            }
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Stock Insuficiente")
            .setMessage(
                if (conflictMessages.isNotEmpty()) {
                    "Los siguientes productos exceden el stock disponible:\n\n${
                        conflictMessages.joinToString("\n")
                    }"
                } else {
                    "Hay productos con stock insuficiente"
                }
            )
            .setPositiveButton("Entendido") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun startCheckoutTimer() {
        checkoutTimer?.cancel()
        checkoutTimer = object : CountDownTimer(5 * 60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Optional: Update UI with remaining time
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = (millisUntilFinished / 1000) % 60
                updateTimerDisplay(minutes, seconds)
            }

            override fun onFinish() {
                handleTimeOut()
            }
        }.start()
    }

    //TODO: Mostrar timer en UI
    private fun updateTimerDisplay(minutes: Long, seconds: Long) {
        // Optional: Show remaining time in UI
        activity?.runOnUiThread {
            // Update timer display if you have one
        }
    }

    private fun handleTimeOut() {
        activity?.runOnUiThread {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Tiempo Agotado")
                .setMessage("El tiempo para completar la compra ha expirado. Los productos han sido liberados.")
                .setPositiveButton("Aceptar") { dialog, _ ->
                    dialog.dismiss()
                    cartManager.clearCart()
                    requireActivity().supportFragmentManager.popBackStack()
                }
                .show()
        }
    }

    private fun navigateToNextStep() {
        val nextFragment = ShoppingCartOrderSummaryFragment1()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, nextFragment)
            .addToBackStack(null)
            .commit()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateCartUI() {
        adapter.notifyDataSetChanged()

        val totalPrice = cartManager.getTotalCartPrice()
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        totalPriceText.text = formatter.format(totalPrice)

        checkoutButton.isEnabled = cartManager.loadCartItems().isNotEmpty()
    }
}