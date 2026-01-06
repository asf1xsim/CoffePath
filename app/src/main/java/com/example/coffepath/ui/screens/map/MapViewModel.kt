package com.example.coffepath.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffepath.model.Cafe
import com.example.coffepath.model.OverpassElement
import com.example.coffepath.repository.ICafeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainMapViewModel @Inject constructor(
    private val repository: ICafeRepository
) : ViewModel(), MapActions {

    private val _uiState = MutableStateFlow<MainMapUIState>(MainMapUIState.Loading)
    val uiState: StateFlow<MainMapUIState> = _uiState.asStateFlow()

    init {
        observeCafes()
    }

    private fun observeCafes() {
        viewModelScope.launch {
            repository.getAllCafes().collect { cafes ->
                _uiState.value = MainMapUIState.Success(cafes)
            }
        }
    }

    override fun loadCafesInArea(south: Double, west: Double, north: Double, east: Double) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is MainMapUIState.Success) {
                _uiState.value = currentState.copy(isLoading = true, error = null)
            }

            val result = repository.fetchCafesFromApi(south, west, north, east)

            result.onSuccess { response ->
                val cafes = response.elements.mapNotNull { element ->
                    convertToCafe(element)
                }
                repository.insertCafes(cafes)
            }.onFailure { exception ->
                val currentState = _uiState.value
                if (currentState is MainMapUIState.Success) {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    private fun convertToCafe(element: OverpassElement): Cafe? {
        if (element.lat == null || element.lon == null || element.tags?.name == null) {
            return null
        }

        val address = buildAddress(
            element.tags.street,
            element.tags.houseNumber,
            element.tags.city
        )

        return Cafe(
            id = element.id,
            name = element.tags.name,
            latitude = element.lat,
            longitude = element.lon,
            address = address,
            openingHours = element.tags.openingHours,
            website = element.tags.website,
            phone = element.tags.phone,
            isFavorite = false
        )
    }

    private fun buildAddress(street: String?, houseNumber: String?, city: String?): String? {
        val parts = mutableListOf<String>()

        if (!street.isNullOrBlank()) {
            val streetPart = if (!houseNumber.isNullOrBlank()) {
                "$street $houseNumber"
            } else {
                street
            }
            parts.add(streetPart)
        }

        if (!city.isNullOrBlank()) {
            parts.add(city)
        }

        return if (parts.isNotEmpty()) parts.joinToString(", ") else null
    }

    override fun onCafeClicked(cafeId: Long) {
        // Navigation will be handled in the UI layer
    }

    override fun toggleFavorite(cafeId: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.updateFavoriteStatus(cafeId, !currentStatus)
        }
    }
}