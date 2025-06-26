package com.fit2081.assignment1.darras

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

/**
 * Object representing the authentication manager
 */
object AuthManager {
    // Private mutable state for the user ID
    private val _userId: MutableState<String?> = mutableStateOf(null)
    // Private value of the clinician key
    private val clinicianKey = "dollar-entry-apples"

    // Login function to set the user ID
    fun login(userId: String) {
        _userId.value = userId
    }

    // Logout function to clear the user ID
    fun logout() {
        _userId.value = null
    }

    // Function to check if the user is logged in and return the user ID
    fun getPatientId(): String? {
        return _userId.value
    }

    // Function to get the clinician key
    fun getClinicianKey(): String {
        return clinicianKey
    }
}