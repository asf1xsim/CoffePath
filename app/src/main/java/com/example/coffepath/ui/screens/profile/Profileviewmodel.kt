package com.example.coffepath.ui.screens.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffepath.repository.PreferencesRepository
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

    private val _uiState = MutableStateFlow(ProfileUIState(isLoading = true))
    val uiState: StateFlow<ProfileUIState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                val userId = preferencesRepository.userIdFlow.first()
                Log.d("ProfileViewModel", "Loading profile for userId: $userId")

                // Создаем или получаем профиль
                val profile = (userProfileRepository as? com.example.coffepath.repository.UserProfileRepositoryImpl)
                    ?.getOrCreateCurrentUserProfile()

                if (profile != null) {
                    Log.d("ProfileViewModel", "Got profile: userId=${profile.userId}, photoUri=${profile.photoUri}")

                    // Сразу показываем профиль
                    val reviews = reviewRepository.getReviewsByUser(userId).first()
                    _uiState.value = ProfileUIState(
                        profile = profile,
                        recentReviews = reviews.take(5),
                        isLoading = false,
                        error = null
                    )

                    // Теперь подписываемся на обновления
                    observeProfileUpdates(userId)
                } else {
                    Log.e("ProfileViewModel", "Failed to create profile")
                    _uiState.value = ProfileUIState(
                        isLoading = false,
                        error = "Failed to load profile"
                    )
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error loading profile", e)
                _uiState.value = ProfileUIState(
                    isLoading = false,
                    error = e.message ?: "Failed to load profile"
                )
            }
        }
    }

    private fun observeProfileUpdates(userId: String) {
        viewModelScope.launch {
            combine(
                userProfileRepository.getUserProfile(userId).filterNotNull(),
                reviewRepository.getReviewsByUser(userId)
            ) { userProfile, reviews ->
                Log.d("ProfileViewModel", "Profile updated: photoUri=${userProfile.photoUri}, reviews=${reviews.size}")
                ProfileUIState(
                    profile = userProfile,
                    recentReviews = reviews.take(5),
                    isLoading = false,
                    error = null
                )
            }.catch { e ->
                Log.e("ProfileViewModel", "Error in profile updates", e)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun updateProfileName(newName: String) {
        viewModelScope.launch {
            try {
                val userId = preferencesRepository.userIdFlow.first()
                Log.d("ProfileViewModel", "Updating name for userId: $userId to: $newName")
                userProfileRepository.updateProfileName(userId, newName)
                _uiState.update { it.copy(isEditingName = false) }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error updating name", e)
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
                val uriString = photoUri?.toString()
                Log.d("ProfileViewModel", "Updating photo for userId: $userId")
                Log.d("ProfileViewModel", "Photo URI: $uriString")
                userProfileRepository.updateProfilePhoto(userId, uriString)
                _uiState.update { it.copy(isEditingPhoto = false) }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error updating photo", e)
                _uiState.update {
                    it.copy(error = "Failed to update photo: ${e.message}")
                }
            }
        }
    }

    fun removeProfilePhoto() {
        Log.d("ProfileViewModel", "Removing profile photo")
        updateProfilePhoto(null)
    }

    fun toggleNameEditing() {
        _uiState.update { it.copy(isEditingName = !it.isEditingName) }
    }

    fun togglePhotoEditing() {
        val newState = !_uiState.value.isEditingPhoto
        Log.d("ProfileViewModel", "Toggle photo editing: $newState")
        _uiState.update { it.copy(isEditingPhoto = newState) }
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