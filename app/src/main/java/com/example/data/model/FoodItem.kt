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

data class Order(
    val id: String = "",
    val items: List<OrderItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val status: String = "PENDING",
    val createdAt: com.google.firebase.Timestamp? = null
)

data class OrderItem(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0
)
