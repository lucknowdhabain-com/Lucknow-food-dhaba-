package com.example.data.repository

import com.example.data.model.FoodItem

class FoodRepository {
    fun getMenuItems(): List<FoodItem> {
        return listOf(
            FoodItem(id = "1", name = "Lucknowi Biryani", price = 250.0, description = "Aromatic basmati rice cooked with succulent pieces of meat and authentic Lucknowi spices."),
            FoodItem(id = "2", name = "Butter Chicken", price = 320.0, description = "Tender chicken cooked in a rich, creamy tomato-based gravy."),
            FoodItem(id = "3", name = "Tanday Kabab (2 pcs)", price = 180.0, description = "Famous melt-in-the-mouth buffalo meat kebabs from the streets of Lucknow."),
            FoodItem(id = "4", name = "Rumali Roti", price = 20.0, description = "Paper-thin soft flatbread."),
            FoodItem(id = "5", name = "Paneer Butter Masala", price = 220.0, description = "Soft paneer cubes in a rich and buttery tomato gravy."),
            FoodItem(id = "6", name = "Garlic Naan", price = 45.0, description = "Leavened flatbread brushed with garlic butter.")
        )
    }
}
