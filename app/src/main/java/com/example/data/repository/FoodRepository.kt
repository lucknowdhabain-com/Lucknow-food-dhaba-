package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.FoodItem
import com.example.data.model.Order
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FoodRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

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

    suspend fun placeOrder(items: List<Map<String, Any>>, totalPrice: Double): Result<String> {
        val userId = Firebase.auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))
        
        val orderData = mapOf(
            "items" to items,
            "totalPrice" to totalPrice,
            "status" to "PENDING",
            "createdAt" to FieldValue.serverTimestamp()
        )

        return try {
            val docRef = db.collection("users").document(userId).collection("orders").add(orderData).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeOrders(): Flow<List<Order>> = callbackFlow {
        val userId = Firebase.auth.currentUser?.uid ?: run {
            close(Exception("User not authenticated"))
            return@callbackFlow
        }

        val registration = db.collection("users")
            .document(userId)
            .collection("orders")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val orders = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Order::class.java)?.copy(id = doc.id)
                    }
                    trySend(orders)
                }
            }
        awaitClose { registration.remove() }
    }
}
