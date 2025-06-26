package com.fit2081.assignment1.darras.network

// Data class representing fruit data
data class FruitData(
    val name: String,
    val family: String,
    // Nested data class representing nutrition information
    val nutritions: Nutrition
)
