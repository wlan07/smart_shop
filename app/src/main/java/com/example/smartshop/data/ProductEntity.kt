package com.example.smartshop.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0
)