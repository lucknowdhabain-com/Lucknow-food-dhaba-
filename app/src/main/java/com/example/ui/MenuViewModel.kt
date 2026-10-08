package com.example.ui

import androidx.lifecycle.ViewModel
import com.example.data.model.CartItem
import com.example.data.model.FoodItem
import com.example.data.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MenuUiState(
    val menuItems: List<FoodItem> = emptyList(),
    val cartItems: Map<String, Int> = emptyMap(), // Item ID to Quantity
    val orderSuccess: Boolean = false
) {
    val totalPrice: Double
        get() = menuItems.sumOf { item ->
            (cartItems[item.id] ?: 0) * item.price
        }

    val totalItems: Int
        get() = cartItems.values.sum()
}

class MenuViewModel(
    private val repository: FoodRepository = FoodRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState(menuItems = repository.getMenuItems()))
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    fun addItem(itemId: String) {
        _uiState.update { currentState ->
            val currentQty = currentState.cartItems[itemId] ?: 0
            currentState.copy(
                cartItems = currentState.cartItems + (itemId to (currentQty + 1))
            )
        }
    }

    fun removeItem(itemId: String) {
        _uiState.update { currentState ->
            val currentQty = currentState.cartItems[itemId] ?: 0
            if (currentQty > 0) {
                val newCartItems = currentState.cartItems.toMutableMap()
                if (currentQty == 1) {
                    newCartItems.remove(itemId)
                } else {
                    newCartItems[itemId] = currentQty - 1
                }
                currentState.copy(cartItems = newCartItems)
            } else {
                currentState
            }
        }
    }

    fun placeOrder() {
        // In a real app, this would send data to Firestore
        _uiState.update { it.copy(orderSuccess = true) }
    }

    fun clearOrderSuccess() {
        _uiState.update { it.copy(orderSuccess = false) }
    }
}
