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
 * ViewModel class for managing food intake data.
 */
class FoodIntakeViewModel(application: Application) : AndroidViewModel(application) {
    // Injecting the repository in the VM
    private val repository = FoodIntakeRepository(
        AppDatabase.getDatabase(application.applicationContext).foodIntakeDao()
    )

    // Private Mutable state flow to hold the list of food intakes
    private val _allFoodIntakes = MutableStateFlow<List<FoodIntake>>(emptyList())
    // Public immutable state flow for observing the list of food intakes
    val allFoodIntake: StateFlow<List<FoodIntake>> = _allFoodIntakes.asStateFlow()

    // Perform refresh on the food intakes from the repository
    init {
        refreshFoodIntakes()
    }

    // Function to refresh the food intakes
    private fun refreshFoodIntakes() {
        viewModelScope.launch {
            repository.allFoodIntakes.collect {
                _allFoodIntakes.value = it
            }
        }
    }

    // Functions to interact with the repository
    fun insertFoodIntake(foodIntake: FoodIntake) {
        viewModelScope.launch {
            repository.insertFoodIntake(foodIntake)
        }
    }
    fun insertFoodIntakes(foodIntakes: List<FoodIntake>) {
        viewModelScope.launch {
            repository.insertFoodIntakes(foodIntakes)
        }
    }
    fun deleteAllFoodIntakes() {
        viewModelScope.launch {
            repository.deleteAllFoodIntakes()
        }
    }
    fun getFoodIntakeById(userId: String, callBack: (FoodIntake?) -> Unit) {
        viewModelScope.launch{
            val foodIntake = repository.getFoodIntakeById(userId)
            callBack(foodIntake)
        }
    }

    // Factory class to create instances of the ViewModel
    class FoodIntakeViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T: ViewModel> create(modelClass: Class<T>): T {
            return FoodIntakeViewModel(application) as T
        }
    }
}