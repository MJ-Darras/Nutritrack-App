package com.fit2081.assignment1.darras.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository class for managing food intake data.
 */
class FoodIntakeRepository(
    // Injecting the DAO in this repo
    private val foodIntakeDao: FoodIntakeDao
) {
    // For the food intake data class and data access object
    val allFoodIntakes: Flow<List<FoodIntake>> = foodIntakeDao.getAllFoodIntakes()

    // Implementation of the functions based on the DAO
    suspend fun insertFoodIntake(foodIntake: FoodIntake) = foodIntakeDao.insertFoodIntake(foodIntake)
    suspend fun insertFoodIntakes(foodIntakes: List<FoodIntake>) = foodIntakeDao.insertFoodIntakes(foodIntakes)
    suspend fun getFoodIntakeById(userId: String) = foodIntakeDao.getIntakeById(userId)
    suspend fun deleteAllFoodIntakes() = foodIntakeDao.deleteAllIntakes()
}