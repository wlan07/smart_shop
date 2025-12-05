package com.example.smartshop.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import java.util.UUID
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ProductRepository(
    private val dao: ProductDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val productsCollection = firestore.collection("products")

    // Room Flow + refresh from Firestore
    fun getProducts(): Flow<List<ProductEntity>> = dao.getAllProductsFlow()

    fun getStats(): Flow<ProductStats> =
        getProducts().map { list ->
            val totalCount = list.size
            val totalValue = list.sumOf { it.quantity * it.price }
            ProductStats(totalCount, totalValue)
        }

    // ----- CRUD -----

    suspend fun addOrUpdateProduct(
        id: String? = null,
        name: String,
        quantity: Int,
        price: Double
    ) {
        require(price > 0) { "Le prix doit être > 0" }
        require(quantity >= 0) { "La quantité doit être ≥ 0" }

        val finalId = id ?: UUID.randomUUID().toString()
        val product = ProductEntity(
            id = finalId,
            name = name,
            quantity = quantity,
            price = price
        )

        // Local
        dao.upsert(product)

        // Cloud
        productsCollection.document(finalId).set(product)
    }

    suspend fun deleteProduct(product: ProductEntity) {
        dao.delete(product)
        productsCollection.document(product.id).delete()
    }

    // Optional: listen Firestore and mirror to Room (safe coroutine usage)
    fun startFirestoreSync(): Flow<Unit> = callbackFlow {
        val scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO)

        val listener = productsCollection.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val products = snapshot.documents.mapNotNull {
                    it.toObject(ProductEntity::class.java)
                }

                scope.launch {
                    dao.deleteAll()
                    products.forEach { dao.upsert(it) }
                }
            }
        }
        awaitClose { listener.remove() }
    }
}

data class ProductStats(
    val totalCount: Int,
    val totalStockValue: Double
)
