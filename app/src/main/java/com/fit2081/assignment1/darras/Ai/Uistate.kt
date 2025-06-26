package com.fit2081.assignment1.darras.Ai

sealed interface Uistate {
    /**
     * Empty state when screen was just shown for the first time
     */
    object Initial: Uistate

    /**
     * Still Loading
     */
    object Loading: Uistate

    /**
     * Text has been generated
     */
    data class Success(val outputText: String) : Uistate

    /**
     * There was an error while generating
     */
    data class Error(val errorMessage: String) : Uistate
}