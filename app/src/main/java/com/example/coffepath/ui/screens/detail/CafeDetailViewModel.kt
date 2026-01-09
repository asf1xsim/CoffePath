package com.example.coffepath.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffepath.repository.ICafeRepository
import com.example.coffepath.repository.IReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CafeDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val cafeRepository: ICafeRepository,
    private val reviewRepository: IReviewRepository
) : ViewModel() {

    private val cafeId: Long = savedStateHandle.get<Long>("cafeId") ?: -1L

    private val _uiState = MutableStateFlow<CafeDetailUIState>(CafeDetailUIState.Loading)
    val uiState: StateFlow<CafeDetailUIState> = _uiState.asStateFlow()

    init {
        loadCafeDetails()
    }

    private fun loadCafeDetails() {
        viewModelScope.launch {
            val cafe = cafeRepository.getCafeById(cafeId)
            if (cafe != null) {
                reviewRepository.getReviewsForCafe(cafeId).collect { reviews ->
                    val averageRating = reviewRepository.getAverageRatingForCafe(cafeId)
                    val reviewCount = reviewRepository.getReviewCountForCafe(cafeId)

                    _uiState.value = CafeDetailUIState.Success(
                        cafe = cafe,
                        reviews = reviews,
                        averageRating = averageRating,
                        reviewCount = reviewCount
                    )
                }
            } else {
                _uiState.value = CafeDetailUIState.NotFound
            }
        }
    }

    fun toggleFavorite(currentStatus: Boolean) {
        viewModelScope.launch {
            cafeRepository.updateFavoriteStatus(cafeId, !currentStatus)
            loadCafeDetails()
        }
    }
}