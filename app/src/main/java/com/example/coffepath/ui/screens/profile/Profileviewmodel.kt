package com.example.coffepath.ui.screens.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffepath.repository.PreferencesRepository
import com.example.coffepath.model.Review
import com.example.coffepath.model.UserProfile
import com.example.coffepath.repository.IReviewRepository
import com.example.coffepath.repository.IUserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userProfileRepository: IUserProfileRepository,
    private val reviewRepository: IReviewRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUIState())
    val uiState: StateFlow<ProfileUIState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                val userId = preferencesRepository.userIdFlow.first()

                userProfileRepository.getUserProfile(userId).collect { profile ->
                    if (profile != null) {
                        reviewRepository.getReviewsByUser(userId).collect { reviews ->
                            _uiState.update {
                                it.copy(
                                    profile = profile,
                                    recentReviews = reviews.take(5),
                                    isLoading = false,
                                    error = null
                                )
                            }
                        }
                    } else {
                        val newProfile = (userProfileRepository as? com.example.coffepath.repository.UserProfileRepositoryImpl)
                            ?.getOrCreateCurrentUserProfile()

                        _uiState.update {
                            it.copy(
                                profile = newProfile,
                                isLoading = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load profile"
                    )
                }
            }
        }
    }

    fun updateProfileName(newName: String) {
        viewModelScope.launch {
            try {
                val userId = preferencesRepository.userIdFlow.first()
                userProfileRepository.updateProfileName(userId, newName)
                _uiState.update { it.copy(isEditingName = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to update name: ${e.message}")
                }
            }
        }
    }

    fun updateProfilePhoto(photoUri: Uri?) {
        viewModelScope.launch {
            try {
                val userId = preferencesRepository.userIdFlow.first()
                userProfileRepository.updateProfilePhoto(userId, photoUri?.toString())
                _uiState.update { it.copy(isEditingPhoto = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to update photo: ${e.message}")
                }
            }
        }
    }

    fun removeProfilePhoto() {
        updateProfilePhoto(null)
    }

    fun toggleNameEditing() {
        _uiState.update { it.copy(isEditingName = !it.isEditingName) }
    }

    fun togglePhotoEditing() {
        _uiState.update { it.copy(isEditingPhoto = !it.isEditingPhoto) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun getStatistics(): Triple<Int, Int, Int> {
        val profile = _uiState.value.profile
        return Triple(
            profile?.reviewsCount ?: 0,
            profile?.favoritesCount ?: 0,
            profile?.visitedCafesCount ?: 0
        )
    }
}