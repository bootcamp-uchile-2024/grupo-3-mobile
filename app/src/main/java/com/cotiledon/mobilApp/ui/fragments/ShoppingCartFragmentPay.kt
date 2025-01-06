package com.cotiledon.mobilApp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.activities.MainContainerActivity
import com.cotiledon.mobilApp.ui.adapters.CartSummaryAdapter
import com.cotiledon.mobilApp.ui.backend.order.RetrofitOrderClient
import com.cotiledon.mobilApp.ui.backend.user.RetrofitUserClient
import com.cotiledon.mobilApp.ui.backend.user.UserApiService
import com.cotiledon.mobilApp.ui.dataClasses.profile.UserProfileUpdate
import com.cotiledon.mobilApp.ui.managers.CartStorageManager
import com.cotiledon.mobilApp.ui.managers.OrderManager
import com.cotiledon.mobilApp.ui.managers.TokenManager
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class ShoppingCartFragmentPay : Fragment(){
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartManager: CartStorageManager
    private lateinit var tokenManager: TokenManager
    private lateinit var userClient: UserApiService

    private lateinit var productPriceText: TextView
    private lateinit var discountValueText: TextView
    private lateinit var shippingCostText: TextView
    private lateinit var totalPriceText: TextView
    private lateinit var discountInput: EditText
    private lateinit var applyDiscountButton: Button
    private lateinit var checkoutButton: Button


    private val SHIPPING_COST = 5000.0
    private val DISCOUNT_CODE = "lanzamientoxapp"
    private val DISCOUNT_PERCENTAGE = 0.20 // 20%
    private var isDiscountApplied = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shopping_cart_pay, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupRecyclerView()
        setupClickListeners()
        updatePrices()
    }

    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.shopping_cart_recycler_view)
        productPriceText = view.findViewById(R.id.product_price)
        discountValueText = view.findViewById(R.id.discount_value)
        shippingCostText = view.findViewById(R.id.shippment_cost)
        totalPriceText = view.findViewById(R.id.total_price)
        discountInput = view.findViewById(R.id.edittext_shopping_discount)
        applyDiscountButton = view.findViewById(R.id.button_shopping_apl)
        checkoutButton = view.findViewById(R.id.btn_ir_a_pagar)

        tokenManager = TokenManager(requireContext())
        cartManager = CartStorageManager(requireContext(), tokenManager)

        val userRetrofitClient = RetrofitUserClient.createUserClient(tokenManager)
    }

    private fun setupRecyclerView() {
        val adapter = CartSummaryAdapter(cartManager.loadCartItems())
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
    }

    private fun setupClickListeners() {
        applyDiscountButton.setOnClickListener {
            handleDiscountCode()
        }

        checkoutButton.setOnClickListener {
            handlePayment()
        }

        applyDiscountButton.setOnClickListener {
            handleDiscountCode()
        }
    }

    private fun handleDiscountCode() {
        val inputCode = discountInput.text.toString().trim()
        if (inputCode.equals(DISCOUNT_CODE, ignoreCase = true) && !isDiscountApplied) {
            isDiscountApplied = true
            updatePrices()
            Toast.makeText(context, "¡Descuento aplicado exitosamente!", Toast.LENGTH_SHORT).show()
        } else if (isDiscountApplied) {
            Toast.makeText(context, "El descuento ya ha sido aplicado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Código de descuento inválido", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updatePrices() {
        val cartItems = cartManager.loadCartItems()

        val productTotal = cartItems.sumOf { it.plantPrice.toDouble() * it.plantQuantity }
        productPriceText.text = formatPrice(productTotal)

        //Shipping cost fijo por mientras el back no implementa eso
        shippingCostText.text = formatPrice(SHIPPING_COST)

        //Mismo con el discount, solo se establece local para probar la funcionalidad
        val discountAmount = if (isDiscountApplied) {
            productTotal * DISCOUNT_PERCENTAGE
        } else {
            0.0
        }
        discountValueText.text = formatPrice(discountAmount)

        val totalPrice = productTotal + SHIPPING_COST - discountAmount
        totalPriceText.text = formatPrice(totalPrice)
    }

    private fun formatPrice(price: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        return formatter.format(price)
    }

    private fun handlePayment() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val userId = tokenManager.getUserId()

                // Handle visitor profile update if necessary
                /*if (tokenManager.isVisitor()) {
                    val visitorDetails = OrderManager.getVisitorDetails()
                    if (visitorDetails != null) {
                        try {
                            val updateResponse = RetrofitUserClient.createUserClient(tokenManager).updateUserProfile(
                                userId = userId,
                                userProfile = UserProfileUpdate(
                                    nombre = visitorDetails.name,
                                    apellido = visitorDetails.lastName,
                                    email = visitorDetails.email,
                                    telefono = visitorDetails.phone,
                                    rut = visitorDetails.rut
                                )
                            )

                            if (!updateResponse.isSuccessful) {
                                Log.e("ShoppingCartFragmentPay", "Failed to update visitor profile")
                                showError("Error updating profile information")
                                return@launch
                            }
                        } catch (e: Exception) {
                            Log.e("ShoppingCartFragmentPay", "Error updating visitor profile", e)
                            showError("Error updating profile information")
                            return@launch
                        }
                    }
                }*/

                // Proceed with order creation
                processOrder(userId)

            } catch (e: Exception) {
                Log.e("ShoppingCartFragmentPay", "Error in handlePayment", e)
                showError("Error processing payment")
            }
        }
    }

    private suspend fun processOrder(userId: Int) {
        val orderRequest = OrderManager.createOrderRequest()

        if (orderRequest != null) {
            val orderClient = RetrofitOrderClient.createOrderClient(tokenManager)
            val response = orderClient.createOrder(userId, orderRequest)

            when {
                response.isSuccessful -> {
                    handleSuccessfulOrder()
                }
                else -> {
                    showError("Error creating order: ${response.code()}")
                }
            }
        } else {
            showError("Missing order details")
        }
    }

    // Handle successful order completion
    private fun handleSuccessfulOrder() {
        // Clear cart and order data
        cartManager.clearCart()
        OrderManager.clearOrderData()

        // Clear visitor details if applicable
        if (tokenManager.isVisitor()) {
            OrderManager.clearVisitorDetails()
        }

        // Update UI
        (activity as? MainContainerActivity)?.updateCartBadge()

        // Navigate to gratitude screen
        val gratitudeFragment = ShoppingCartGratitudeFragment.newInstance()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, gratitudeFragment)
            .commit()
    }

    // Helper method for showing errors
    private fun showError(message: String) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun newInstance() = ShoppingCartFragmentPay()
    }
}