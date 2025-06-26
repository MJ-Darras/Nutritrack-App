package com.fit2081.assignment1.darras.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Repository class for fetching fruit data from the API
class FruitRepository {
    // Instance of the FruitApiService
    private val fruitApi = FruitApiService.create()
    // Function to fetch fruit data by name
    suspend fun fetchFruit(fruitName:String): Result<FruitData>{
        return withContext(Dispatchers.IO){
            // Attempt to fetch fruit data from the API
            try {
                val result = fruitApi.getFruit(fruitName.trim().lowercase())
                Result.success(result)
            } catch (e:Exception) { // Handle exceptions
                Result.failure(e)
            }
        }
    }
}