package com.fit2081.assignment1.darras.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

/**
 * Repository class for managing patient data.
 */
class PatientRepository (context: Context){
    // Injecting the DAO in this repo
    private val patientDao = AppDatabase.getDatabase(context).patientDao()

    // Immutable state flow for the different lists of patients
    val allPatients: Flow<List<Patient>> = patientDao.getAllPatients()
    val allPatientIds: Flow<List<String>> = patientDao.getAllPatientIds()
    val allRegisteredPatients: Flow<List<Patient>> = patientDao.getRegisteredPatients()
    val allUnregisteredPatients: Flow<List<Patient>> = patientDao.getUnregisteredPatients()

    // Implementation of the functions based on the DAO
    suspend fun insertPatient(patient: Patient) = patientDao.insertPatient(patient)
    suspend fun insertPatients(patients: List<Patient>) = patientDao.insertPatients(patients)
    suspend fun getPatientById(userId: String) = patientDao.getPatientById(userId)
    suspend fun deleteAllPatients() = patientDao.deleteAllPatients()
    suspend fun updateAiResponses(userId: String, responses: String) =
        patientDao.updateAiResponses(userId, responses)
    suspend fun getAiResponsesById(userId: String):String? = patientDao.getAiResponsesById(userId)
}