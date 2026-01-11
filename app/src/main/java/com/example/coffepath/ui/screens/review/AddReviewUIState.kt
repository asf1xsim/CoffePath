package com.example.coffepath.ui.screens.review

import android.net.Uri
import com.example.coffepath.mlkit.model.DrinkType
import com.example.coffepath.mlkit.model.MenuOCRResult

data class AddReviewUIState(
    val cafeName: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val photoUri: Uri? = null,
    val menuPhotoUri: Uri? = null,
    val detectedDrinkType: DrinkType? = null,
    val menuOCRResult: MenuOCRResult? = null,
    val isClassifying: Boolean = false,
    val isRecognizingMenu: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
) {
    val isValid: Boolean
        get() = rating > 0 && comment.isNotBlank()
}