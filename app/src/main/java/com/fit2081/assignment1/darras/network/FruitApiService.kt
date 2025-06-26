package com.fit2081.assignment1.darras.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// Interface for the Fruit API
interface FruitApiService {
    // Define the endpoint for getting fruit data by name
    @GET("api/fruit/{fruitName}")
    // Function to fetch fruit data by name
    suspend fun getFruit(@Path("fruitName") name: String): FruitData
    // Companion object to create an instance of the FruitApiService
    companion object {
        fun create(): FruitApiService {
            return Retrofit.Builder()
                .baseUrl("https://www.fruityvice.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FruitApiService::class.java)
        }
    }
}