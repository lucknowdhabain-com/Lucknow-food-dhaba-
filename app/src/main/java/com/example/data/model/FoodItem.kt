package com.example.data.model

data class FoodItem(
    val id: String,
    val name: String,
    val price: Double,
    val description: String = "",
    val imageUrl: String? = null
)

data class CartItem(
    val foodItem: FoodItem,
    val quantity: Int
)
