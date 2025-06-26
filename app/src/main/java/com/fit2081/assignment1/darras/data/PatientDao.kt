package com.fit2081.assignment1.darras.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for interacting with the patient data in the database.
 */
@Dao
interface PatientDao {
    @Query("SELECT * FROM patients ORDER BY user_id DESC")
    fun getAllPatients(): Flow<List<Patient>>

    @Query("SELECT user_id FROM patients ORDER BY user_id DESC")
    fun getAllPatientIds(): Flow<List<String>>

    @Query("SELECT * FROM patients WHERE registered = 1")
    fun getRegisteredPatients(): Flow<List<Patient>>

    @Query("SELECT * FROM patients WHERE registered = 0")
    fun getUnregisteredPatients(): Flow<List<Patient>>

    @Query("SELECT aiResponses FROM patients WHERE user_id=:userId LIMIT 1")
    suspend fun getAiResponsesById(userId: String): String?

    @Query("UPDATE patients SET aiResponses =:responses WHERE user_id =:userId")
    suspend fun updateAiResponses(userId: String, responses : String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: Patient): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatients(patients: List<Patient>): List<Long>

    @Query("SELECT * FROM patients WHERE user_id =:userId LIMIT 1")
    suspend fun getPatientById(userId: String): Patient?

    @Query("DELETE FROM patients")
    suspend fun deleteAllPatients()
}