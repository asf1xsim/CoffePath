package com.example.coffepath.ui.screens.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffepath.repository.ICafeRepository
import com.example.coffepath.repository.IReviewRepository
import com.example.coffepath.repository.IUserProfileRepository
import com.example.coffepath.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CafeDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val cafeRepository: ICafeRepository,
    private val reviewRepository: IReviewRepository,
    private val userProfileRepository: IUserProfileRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val cafeId: Long = savedStateHandle.get<Long>("cafeId") ?: -1L

    private val _uiState = MutableStateFlow<CafeDetailUIState>(CafeDetailUIState.Loading)
    val uiState: StateFlow<CafeDetailUIState> = _uiState.asStateFlow()

    init {
        loadCafeDetails()
    }

    private fun loadCafeDetails() {
        viewModelScope.launch {
            Log.d("CafeDetailViewModel", "Starting to observe cafe with id: $cafeId")
            combine(
                cafeRepository.observeCafeById(cafeId).filterNotNull(),
                reviewRepository.getReviewsForCafe(cafeId)
            ) { cafe, reviews ->
                val averageRating = reviewRepository.getAverageRatingForCafe(cafeId)
                val reviewCount = reviewRepository.getReviewCountForCafe(cafeId)

                Log.d("CafeDetailViewModel", "=== Cafe updated ===")
                Log.d("CafeDetailViewModel", "Name: ${cafe.name}")
                Log.d("CafeDetailViewModel", "Menu JSON present: ${cafe.menuJson != null}")
                if (cafe.menuJson != null) {
                    Log.d("CafeDetailViewModel", "Menu JSON length: ${cafe.menuJson.length}")
                    Log.d("CafeDetailViewModel", "Menu JSON preview: ${cafe.menuJson.take(100)}")
                } else {
                    Log.d("CafeDetailViewModel", "Menu JSON is NULL")
                }
                Log.d("CafeDetailViewModel", "====================")

                CafeDetailUIState.Success(
                    cafe = cafe,
                    reviews = reviews,
                    averageRating = averageRating,
                    reviewCount = reviewCount
                )
            }.catch { exception ->
                Log.e("CafeDetailViewModel", "Error loading cafe details", exception)
                _uiState.value = CafeDetailUIState.NotFound
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleFavorite(currentStatus: Boolean) {
        viewModelScope.launch {
            try {
                val newStatus = !currentStatus
                cafeRepository.updateFavoriteStatus(cafeId, newStatus)
                val userId = preferencesRepository.userIdFlow.first()
                if (newStatus) {
                    Log.d("CafeDetailViewModel", "Incrementing favorites count for user: $userId")
                    userProfileRepository.incrementFavoritesCount(userId)
                } else {
                    Log.d("CafeDetailViewModel", "Decrementing favorites count for user: $userId")
                    userProfileRepository.decrementFavoritesCount(userId)
                }
            } catch (e: Exception) {
                Log.e("CafeDetailViewModel", "Error toggling favorite", e)
            }
        }
    }
}