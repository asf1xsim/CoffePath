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

enum class DrinkType(val displayName: String, val keywords: List<String>) {
    ESPRESSO(
        "Espresso",
        listOf("espresso", "coffee", "caffeine", "coffee cup", "cup")
    ),
    CAPPUCCINO(
        "Cappuccino",
        listOf("cappuccino", "coffee", "foam", "milk", "latte art", "froth")
    ),
    LATTE(
        "Latte",
        listOf("latte", "coffee", "milk", "latte art", "cafe latte")
    ),
    AMERICANO(
        "Americano",
        listOf("americano", "black coffee", "coffee", "caffeine")
    ),
    MACCHIATO(
        "Macchiato",
        listOf("macchiato", "coffee", "espresso macchiato", "foam")
    ),
    MOCHA(
        "Mocha",
        listOf("mocha", "chocolate", "coffee", "cocoa")
    ),
    FLAT_WHITE(
        "Flat White",
        listOf("flat white", "coffee", "milk", "microfoam")
    ),
    TEA(
        "Tea",
        listOf("tea", "tea cup", "herbal tea", "green tea", "black tea")
    ),
    HOT_CHOCOLATE(
        "Hot Chocolate",
        listOf("hot chocolate", "chocolate", "cocoa", "chocolate drink")
    ),
    ICED_COFFEE(
        "Iced Coffee",
        listOf("iced coffee", "cold brew", "ice", "coffee")
    ),
    FRAPPE(
        "Frappe",
        listOf("frappe", "frappuccino", "blended", "ice", "coffee")
    ),
    UNKNOWN(
        "Unknown Drink",
        listOf()
    );

    companion object {
        fun fromLabels(labels: List<DetectedLabel>): Pair<DrinkType, Float> {
            if (labels.isEmpty()) {
                return UNKNOWN to 0f
            }
            val labelTexts = labels.map { it.text.lowercase() }

            val scores = mutableMapOf<DrinkType, Float>()

            for (drinkType in entries.filter { it != UNKNOWN }) {
                var score = 0f
                var matchCount = 0

                for (keyword in drinkType.keywords) {
                    for (label in labels) {
                        if (label.text.lowercase().contains(keyword)) {
                            score += label.confidence
                            matchCount++
                        }
                    }
                }

                if (matchCount > 0) {
                    scores[drinkType] = score / matchCount
                }
            }

            val bestMatch = scores.maxByOrNull { it.value }

            return if (bestMatch != null && bestMatch.value > 0.3f) {
                bestMatch.key to bestMatch.value
            } else {
                UNKNOWN to 0f
            }
        }

        fun fromString(name: String): DrinkType {
            return entries.find {
                it.displayName.equals(name, ignoreCase = true)
            } ?: UNKNOWN
        }
    }
}