package com.fit2081.assignment1.darras.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for interacting with the food intake data in the database.
 */
@Dao
interface FoodIntakeDao {
    @Query("SELECT * FROM foodIntakeAnswers ORDER BY patient_user_id DESC")
    fun getAllFoodIntakes(): Flow<List<FoodIntake>>

    @Query("SELECT * FROM foodIntakeAnswers WHERE patient_user_id =:userId LIMIT 1")
    suspend fun getIntakeById(userId: String): FoodIntake?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodIntake(foodIntake: FoodIntake): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodIntakes(foodIntakes: List<FoodIntake>): List<Long>

    @Query("DELETE FROM foodIntakeAnswers")
    suspend fun deleteAllIntakes()
}