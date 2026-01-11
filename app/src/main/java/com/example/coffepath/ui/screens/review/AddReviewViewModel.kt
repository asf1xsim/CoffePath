package com.example.coffepath.ui.screens.review

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffepath.mlkit.DrinkClassifier
import com.example.coffepath.mlkit.MenuOCRReader
import com.example.coffepath.mlkit.model.DrinkType
import com.example.coffepath.mlkit.model.MenuOCRResult
import com.example.coffepath.model.Review
import com.example.coffepath.repository.ICafeRepository
import com.example.coffepath.repository.IReviewRepository
import com.example.coffepath.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class AddReviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val cafeRepository: ICafeRepository,
    private val reviewRepository: IReviewRepository,
    private val drinkClassifier: DrinkClassifier,
    private val menuOCRReader: MenuOCRReader,
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
    fun setMenuPhoto(uri: Uri, autoRecognize: Boolean = true) {
        _uiState.update { it.copy(menuPhotoUri = uri) }
        if (autoRecognize) {
            recognizeMenu(uri)
        }
    }
    fun removeMenuPhoto() {
        _uiState.update {
            it.copy(
                menuPhotoUri = null,
                menuOCRResult = null
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

    fun recognizeMenu(imageUri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRecognizingMenu = true, error = null) }

            menuOCRReader.recognizeMenu(imageUri)
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            menuOCRResult = result,
                            isRecognizingMenu = false
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isRecognizingMenu = false,
                            error = "Failed to recognize menu: ${exception.message}"
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

    fun rerecognizeMenu() {
        _uiState.value.menuPhotoUri?.let { uri ->
            recognizeMenu(uri)
        }
    }

    fun setDrinkType(drinkType: DrinkType) {
        _uiState.update { it.copy(detectedDrinkType = drinkType) }
    }

    fun clearDrinkType() {
        _uiState.update { it.copy(detectedDrinkType = null) }
    }

    private suspend fun saveMenuToCafe(cafeId: Long, menuResult: MenuOCRResult) {
        try {
            val cafe = cafeRepository.getCafeById(cafeId)
            if (cafe != null) {
                val menuJson = Json.encodeToString(menuResult)
                Log.d("AddReviewViewModel", "Saving menu JSON (${menuJson.length} chars) for cafe $cafeId")
                Log.d("AddReviewViewModel", "Menu preview: ${menuJson.take(200)}")

                cafeRepository.updateCafeMenu(cafeId, menuJson)

                val verifiedCafe = cafeRepository.getCafeById(cafeId)
                Log.d("AddReviewViewModel", "Menu saved and verified: ${verifiedCafe?.menuJson != null}")
                if (verifiedCafe?.menuJson != null) {
                    Log.d("AddReviewViewModel", "Verified menu JSON length: ${verifiedCafe.menuJson.length}")
                }
            } else {
                Log.e("AddReviewViewModel", "Cafe not found with id: $cafeId")
            }
        } catch (e: Exception) {
            Log.e("AddReviewViewModel", "Failed to save menu to cafe", e)
        }
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
                Log.d("AddReviewViewModel", "Saving review for userId: $userId, cafeId: $cafeId")

                val review = Review(
                    cafeId = cafeId,
                    userId = userId,
                    rating = currentState.rating,
                    comment = currentState.comment,
                    photoUri = currentState.photoUri?.toString(),
                    drinkType = currentState.detectedDrinkType?.displayName,
                    timestamp = System.currentTimeMillis()
                )

                val reviewId = reviewRepository.insertReview(review)
                Log.d("AddReviewViewModel", "Review inserted with id: $reviewId")

                currentState.menuOCRResult?.let { menuResult ->
                    Log.d("AddReviewViewModel", "Saving menu with ${menuResult.detectedItems.size} items")
                    saveMenuToCafe(cafeId, menuResult)
                }

                Log.d("AddReviewViewModel", "Review save completed successfully")
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isSuccess = true
                    )
                }
            } catch (e: Exception) {
                Log.e("AddReviewViewModel", "Failed to save review", e)
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