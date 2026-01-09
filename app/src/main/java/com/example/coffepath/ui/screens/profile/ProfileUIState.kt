package com.example.coffepath.ui.screens.profile

import com.example.coffepath.model.Review
import com.example.coffepath.model.UserProfile

data class ProfileUIState(
    val profile: UserProfile? = null,
    val recentReviews: List<Review> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val isEditingName: Boolean = false,
    val isEditingPhoto: Boolean = false
)