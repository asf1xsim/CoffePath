package com.example.coffepath.ui.screens.review

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffepath.mlkit.DrinkClassifier
import com.example.coffepath.mlkit.model.DrinkType
import com.example.coffepath.model.Review
import com.example.coffepath.repository.ICafeRepository
import com.example.coffepath.repository.IReviewRepository
import com.example.coffepath.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddReviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val cafeRepository: ICafeRepository,
    private val reviewRepository: IReviewRepository,
    private val drinkClassifier: DrinkClassifier,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val cafeId: Long = savedStateHandle.get<Long>("cafeId") ?: -1L

    private val _uiState = MutableStateFlow(AddReviewUIState())
    val uiState: StateFlow<AddReviewUIState> = _uiState.asStateFlow()

    init {
        loadCafeName()
    }

    private fun loadCafeName() {
        viewModelScope.launch {
            val cafe = cafeRepository.getCafeById(cafeId)
            if (cafe != null) {
                _uiState.update { it.copy(cafeName = cafe.name) }
            }
        }
    }

    fun updateRating(rating: Float) {
        _uiState.update { it.copy(rating = rating) }
    }

    fun updateComment(comment: String) {
        _uiState.update { it.copy(comment = comment) }
    }

    fun setPhoto(uri: Uri, autoClassify: Boolean = true) {
        _uiState.update { it.copy(photoUri = uri) }

        if (autoClassify) {
            classifyDrink(uri)
        }
    }

    fun removePhoto() {
        _uiState.update {
            it.copy(
                photoUri = null,
                detectedDrinkType = null
            )
        }
    }

    fun classifyDrink(imageUri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isClassifying = true) }

            drinkClassifier.classifyDrink(imageUri)
                .onSuccess { result ->
                    val drinkType = if (result.confidence >= 0.6f) {
                        result.drinkType
                    } else {
                        null
                    }

                    _uiState.update {
                        it.copy(
                            detectedDrinkType = drinkType,
                            isClassifying = false
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isClassifying = false,
                            error = "Failed to classify drink: ${exception.message}"
                        )
                    }
                }
        }
    }

    fun reclassifyDrink() {
        _uiState.value.photoUri?.let { uri ->
            classifyDrink(uri)
        }
    }

    fun setDrinkType(drinkType: DrinkType) {
        _uiState.update { it.copy(detectedDrinkType = drinkType) }
    }

    fun clearDrinkType() {
        _uiState.update { it.copy(detectedDrinkType = null) }
    }

    fun saveReview() {
        viewModelScope.launch {
            val currentState = _uiState.value

            if (!currentState.isValid) {
                _uiState.update { it.copy(error = "Please provide rating and comment") }
                return@launch
            }

            _uiState.update { it.copy(isSaving = true, error = null) }

            try {
                val userId = preferencesRepository.userIdFlow.first()
                val review = Review(
                    cafeId = cafeId,
                    userId = userId,
                    rating = currentState.rating,
                    comment = currentState.comment,
                    photoUri = currentState.photoUri?.toString(),
                    drinkType = currentState.detectedDrinkType?.displayName,
                    timestamp = System.currentTimeMillis()
                )

                reviewRepository.insertReview(review)

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isSuccess = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = "Failed to save review: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    override fun onCleared() {
        super.onCleared()
    }
}