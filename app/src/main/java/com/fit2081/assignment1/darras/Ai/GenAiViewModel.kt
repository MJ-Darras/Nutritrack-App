package com.fit2081.assignment1.darras.Ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.fit2081.assignment1.darras.BuildConfig
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Viewmodel class for handling interactions with a generative AI model.
 * It manages UI state and communicates with the generative model.
 */
class GenAiViewModel: ViewModel() {

    /**
     * Mutable state flow to hold the current UI state.
     */
    private val _uiState: MutableStateFlow<Uistate> =
        MutableStateFlow(Uistate.Initial)

    /**
     * Publicly exposed immutable state flow for observing the UI state.
     */
    val uiState: StateFlow<Uistate> =
        _uiState.asStateFlow()

    /**
     * Instance of the GenerativeModel used to generate content.
     */
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )

    /**
     * Sends a prompt to the AI model and updates the state
     */
    fun sendPrompt(
        prompt: String
    ) {
        // Set the UI state to loading before retrieving
        _uiState.value = Uistate.Loading

        // Launch a coroutine in the IO dispatchers to perform API call
        viewModelScope.launch(Dispatchers.IO) {
            try{
                // Generate content
                val response = generativeModel.generateContent(
                    content{
                        text(prompt)
                    }
                )
                // Update UI
                response.text?.let {outputContent ->
                    _uiState.value = Uistate.Success(outputContent)
                }
            } catch (e: Exception){
                // Handle any errors
                _uiState.value = Uistate.Error(e.localizedMessage ?: "")
            }
        }
    }

}