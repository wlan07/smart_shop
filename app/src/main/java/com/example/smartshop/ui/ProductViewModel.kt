package com.example.smartshop.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshop.data.AppDatabase
import com.example.smartshop.data.ProductEntity
import com.example.smartshop.data.ProductRepository
import com.example.smartshop.data.ProductStats
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductViewModel(app: Application) : AndroidViewModel(app) {

    private val repository: ProductRepository

    val products: StateFlow<List<ProductEntity>>
    val stats: StateFlow<ProductStats>

    init {
        val db = AppDatabase.getInstance(app)
        repository = ProductRepository(db.productDao())

        products = repository.getProducts()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

        stats = repository.getStats()
            .stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                ProductStats(0, 0.0)
            )

        // Start Firestore sync (optional)
        viewModelScope.launch {
            repository.startFirestoreSync().collect { }
        }
    }

    fun saveProduct(id: String?, name: String, qty: Int, price: Double, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.addOrUpdateProduct(id, name, qty, price)
            } catch (e: IllegalArgumentException) {
                onError(e.message ?: "Erreur de validation")
            }
        }
    }

    fun delete(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }
}
