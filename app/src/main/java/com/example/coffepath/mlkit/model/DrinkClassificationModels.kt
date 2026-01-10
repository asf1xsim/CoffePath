package com.example.coffepath.mlkit.model

import java.io.Serializable

data class DrinkClassificationResult(
    val drinkType: DrinkType,
    val confidence: Float,
    val rawLabels: List<DetectedLabel>
) : Serializable {
    fun isConfident(): Boolean = confidence >= 0.7f

    fun getDisplayName(): String = drinkType.displayName
}

data class DetectedLabel(
    val text: String,
    val confidence: Float,
    val index: Int
) : Serializable

data class KeywordWeight(
    val keyword: String,
    val weight: Float,
    val type: KeywordType = KeywordType.PRIMARY
)

enum class KeywordType {
    PRIMARY,
    SECONDARY,
    NEGATIVE
}