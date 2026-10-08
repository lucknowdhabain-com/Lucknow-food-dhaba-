package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CartItem
import com.example.data.model.FoodItem
import com.example.data.model.Order
import com.example.data.repository.FoodRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MenuUiState(
    val menuItems: List<FoodItem> = emptyList(),
    val cartItems: Map<String, Int> = emptyMap(), // Item ID to Quantity
    val orders: List<Order> = emptyList(),
    val orderSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val totalPrice: Double
        get() = menuItems.sumOf { item ->
            (cartItems[item.id] ?: 0) * item.price
        }

    val totalItems: Int
        get() = cartItems.values.sum()
}

class MenuViewModel(
    private val repository: FoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState(menuItems = repository.getMenuItems()))
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeOrders()
                .catch { e -> _uiState.update { it.copy(errorMessage = e.message) } }
                .collect { orders ->
                    _uiState.update { it.copy(orders = orders) }
                }
        }
    }

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
        val currentState = _uiState.value
        val itemsToOrder = currentState.menuItems.filter { currentState.cartItems.containsKey(it.id) }.map {
            mapOf(
                "id" to it.id,
                "name" to it.name,
                "price" to it.price,
                "quantity" to (currentState.cartItems[it.id] ?: 0)
            )
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = repository.placeOrder(itemsToOrder, currentState.totalPrice)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, orderSuccess = true, cartItems = emptyMap()) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun clearOrderSuccess() {
        _uiState.update { it.copy(orderSuccess = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
