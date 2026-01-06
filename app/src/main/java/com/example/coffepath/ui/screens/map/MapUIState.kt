package com.example.coffepath.ui.screens.map

import com.example.coffepath.model.Cafe
import java.io.Serializable

sealed class MainMapUIState : Serializable {
    object Loading : MainMapUIState()
    data class Success(
        val cafes: List<Cafe>,
        val isLoading: Boolean = false,
        val error: String? = null
    ) : MainMapUIState()
    data class Error(val message: String) : MainMapUIState()
}