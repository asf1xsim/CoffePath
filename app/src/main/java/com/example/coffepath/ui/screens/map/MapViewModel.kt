package com.example.coffepath.ui.screens.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffepath.model.*
import com.example.coffepath.repository.ICafeRepository
import com.example.coffepath.repository.IReviewRepository
import com.example.coffepath.repository.IUserProfileRepository
import com.example.coffepath.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainMapViewModel @Inject constructor(
    private val cafeRepository: ICafeRepository,
    private val reviewRepository: IReviewRepository,
    private val userProfileRepository: IUserProfileRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel(), MapActions {

    private val _uiState = MutableStateFlow<MainMapUIState>(MainMapUIState.Loading)
    val uiState: StateFlow<MainMapUIState> = _uiState.asStateFlow()

    init {
        observeCafes()
    }

    private fun observeCafes() {
        viewModelScope.launch {
            combine(
                cafeRepository.getAllCafes(),
                reviewRepository.getAllReviews()
            ) { cafes, _ ->
                cafes.map { cafe ->
                    val averageRating = reviewRepository.getAverageRatingForCafe(cafe.id)
                    val reviewsCount = reviewRepository.getReviewCountForCafe(cafe.id)
                    val isOpen = parseOpeningHours(cafe.openingHours)

                    CafeMapItem(
                        cafe = cafe,
                        averageRating = averageRating,
                        reviewsCount = reviewsCount,
                        isOpen = isOpen
                    )
                }
            }.collect { cafeItems ->
                val currentState = _uiState.value
                if (currentState is MainMapUIState.Success) {
                    val filteredItems = applyFilters(cafeItems, currentState.filter)
                    _uiState.value = currentState.copy(
                        cafeItems = cafeItems,
                        filteredItems = filteredItems
                    )
                } else {
                    _uiState.value = MainMapUIState.Success(
                        cafeItems = cafeItems,
                        filteredItems = cafeItems
                    )
                }
            }
        }
    }

    override fun loadCafesInArea(south: Double, west: Double, north: Double, east: Double) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is MainMapUIState.Success) {
                _uiState.value = currentState.copy(isLoading = true, error = null)
            }

            val result = cafeRepository.fetchCafesFromApi(south, west, north, east)

            result.onSuccess { response ->
                val cafes = response.elements.mapNotNull { element ->
                    convertToCafe(element)
                }
                cafeRepository.insertCafes(cafes)
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

    }

    override fun toggleFavorite(cafeId: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            try {
                val newStatus = !currentStatus

                cafeRepository.updateFavoriteStatus(cafeId, newStatus)
                val userId = preferencesRepository.userIdFlow.first()
                if (newStatus) {
                    Log.d("MapViewModel", "Incrementing favorites count for user: $userId")
                    userProfileRepository.incrementFavoritesCount(userId)
                } else {
                    Log.d("MapViewModel", "Decrementing favorites count for user: $userId")
                    userProfileRepository.decrementFavoritesCount(userId)
                }
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error toggling favorite", e)
            }
        }
    }

    fun updateFilter(filter: MapFilterState) {
        val currentState = _uiState.value
        if (currentState is MainMapUIState.Success) {
            val filteredItems = applyFilters(currentState.cafeItems, filter)
            _uiState.value = currentState.copy(
                filter = filter,
                filteredItems = filteredItems
            )
        }
    }

    fun clearFilters() {
        updateFilter(MapFilterState())
    }

    private fun applyFilters(items: List<CafeMapItem>, filter: MapFilterState): List<CafeMapItem> {
        if (!filter.hasActiveFilters()) {
            return items
        }

        return items.filter { it.matchesFilter(filter) }
    }

    fun toggleFilterSheet() {
        val currentState = _uiState.value
        if (currentState is MainMapUIState.Success) {
            _uiState.value = currentState.copy(
                showFilterSheet = !currentState.showFilterSheet
            )
        }
    }

    fun updateMapSettings(settings: MapSettings) {
        val currentState = _uiState.value
        if (currentState is MainMapUIState.Success) {
            _uiState.value = currentState.copy(mapSettings = settings)
        }
    }

    fun toggleClustering() {
        val currentState = _uiState.value
        if (currentState is MainMapUIState.Success) {
            val newSettings = currentState.mapSettings.copy(
                enableClustering = !currentState.mapSettings.enableClustering
            )
            _uiState.value = currentState.copy(mapSettings = newSettings)
        }
    }

    fun clearError() {
        val currentState = _uiState.value
        if (currentState is MainMapUIState.Success) {
            _uiState.value = currentState.copy(error = null)
        }
    }

    private fun parseOpeningHours(openingHours: String?): Boolean? {
        if (openingHours.isNullOrBlank()) {
            return null
        }

        if (openingHours.contains("24/7", ignoreCase = true)) {
            return true
        }

        return null
    }
}