package com.example.coffepath.ui.screens.detail

import com.example.coffepath.model.Cafe
import com.example.coffepath.model.Review

sealed class CafeDetailUIState {
    object Loading : CafeDetailUIState()
    data class Success(
        val cafe: Cafe,
        val reviews: List<Review>,
        val averageRating: Float?,
        val reviewCount: Int
    ) : CafeDetailUIState()
    object NotFound : CafeDetailUIState()
}