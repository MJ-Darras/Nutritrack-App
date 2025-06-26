package com.fit2081.assignment1.darras.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel class for managing patient data.
 */
class PatientsViewModel(application: Application) : AndroidViewModel(application) {
    // Injecting the repository in the VM
    private val patientRepository: PatientRepository = PatientRepository(application)

    // Private Mutable state flow to hold the list of patients
    private val _allPatients = MutableStateFlow<List<Patient>>(emptyList())
    // Public immutable state flow for observing the list of patients
    val allPatients: StateFlow<List<Patient>> = _allPatients.asStateFlow()

    // Private Mutable state flow to hold the list of registered patients
    private val _allRegisteredPatients = MutableStateFlow<List<Patient>>(emptyList())
    // Public immutable state flow for observing the list of registered patients
    val allRegisteredPatients: StateFlow<List<Patient>> = _allRegisteredPatients.asStateFlow()

    // Private Mutable state flow to hold the list of unregistered patients
    private val _allUnregisteredPatients = MutableStateFlow<List<Patient>>(emptyList())
    // Public immutable state flow for observing the list of unregistered patients
    val allUnregisteredPatients: StateFlow<List<Patient>> = _allUnregisteredPatients.asStateFlow()

    // Perform refresh on the patients from the repository
    init{
        refreshPatients()
    }

    // Function to refresh the different lists of patients
    private fun refreshPatients() {
        viewModelScope.launch {
            launch{
                patientRepository.allPatients.collect {
                    _allPatients.value = it
                    println("Patients refereshed: ${_allPatients.value.size}")
                }

            }
            launch {
                patientRepository.allRegisteredPatients.collect{
                    _allRegisteredPatients.value = it
                    println("Registered Patients refereshed: ${_allRegisteredPatients.value.size}")
                }
            }
            launch {
                patientRepository.allUnregisteredPatients.collect{
                    _allUnregisteredPatients.value = it
                    println("Registered Patients refereshed: ${_allRegisteredPatients.value.size}")
                }
            }
        }
    }

    // Functions to interact with the repository
    fun insertPatient(patient: Patient) {
        viewModelScope.launch {
            patientRepository.insertPatient(patient)
        }
    }
    fun insertPatients(patients: List<Patient>) {
        viewModelScope.launch {
            patientRepository.insertPatients(patients)
        }
    }
    fun deleteAllPatients() {
        viewModelScope.launch {
            patientRepository.deleteAllPatients()
            refreshPatients()
        }
    }
    fun getPatientById(userId: String, callback: (Patient?) -> Unit) {
        viewModelScope.launch {
            val patient = patientRepository.getPatientById(userId)
            callback(patient)
        }
    }
    fun getAiResponsesById(userId: String, callback: (List<String>) -> Unit){
        viewModelScope.launch {
            val rawData = patientRepository.getAiResponsesById(userId)
            //find the result
            // if raw data exists, then split it
            // then filter blanks
            // if nothing comes up return empty list
            val result = rawData?.split("||")?.filter { it.isNotBlank() }?: emptyList()
            callback(result)
        }

    }
    fun updateAiResponses(userId: String, newResponse: String){
        viewModelScope.launch {
            // get the patient and check if it exists
            val patient = patientRepository.getPatientById(userId)
            if(patient != null){
                // check if the aiResponses is blank
                val delimeter = "||"
                val updatedResponses = if(patient.aiResponses.isBlank()){
                    // if it is blank, just add the new response
                    newResponse
                } else {
                    // if it is not blank, add the new response to the existing responses using delimiter
                    patient.aiResponses + delimeter + newResponse
                }
                // update the patient with the new responses
                patientRepository.updateAiResponses(userId, updatedResponses)
            }
        }
    }

    // A factory class to create instances of the ViewModel
    class PatientsViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T: ViewModel> create(modelClass: Class<T>): T {
            return PatientsViewModel(application) as T
        }
    }
}