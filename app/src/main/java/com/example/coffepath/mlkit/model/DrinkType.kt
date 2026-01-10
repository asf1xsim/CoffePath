package com.example.coffepath.mlkit.model

enum class DrinkType(
    val displayName: String,
    val keywords: List<KeywordWeight>,
    val visualFeatures: List<String> = emptyList()
) {
    CAPPUCCINO(
        "Cappuccino",
        listOf(
            KeywordWeight("cappuccino", 3.0f, KeywordType.PRIMARY),
            KeywordWeight("foam", 2.0f, KeywordType.PRIMARY),
            KeywordWeight("froth", 2.0f, KeywordType.PRIMARY),
            KeywordWeight("milk foam", 2.5f, KeywordType.PRIMARY),
            KeywordWeight("coffee", 1.0f, KeywordType.SECONDARY),
            KeywordWeight("milk", 1.5f, KeywordType.SECONDARY),
            KeywordWeight("latte art", 1.2f, KeywordType.SECONDARY),
            KeywordWeight("cream", 1.3f, KeywordType.SECONDARY),
            KeywordWeight("cup", 0.5f, KeywordType.SECONDARY),
            KeywordWeight("espresso", -0.5f, KeywordType.NEGATIVE)
        ),
        listOf("white", "cream", "foam", "light brown")
    ),
    LATTE(
        "Latte",
        listOf(
            KeywordWeight("latte", 3.0f, KeywordType.PRIMARY),
            KeywordWeight("cafe latte", 3.0f, KeywordType.PRIMARY),
            KeywordWeight("latte art", 2.5f, KeywordType.PRIMARY),
            KeywordWeight("milk", 2.0f, KeywordType.PRIMARY),
            KeywordWeight("coffee", 1.0f, KeywordType.SECONDARY),
            KeywordWeight("cream", 1.5f, KeywordType.SECONDARY),
            KeywordWeight("cup", 0.5f, KeywordType.SECONDARY),
            KeywordWeight("foam", -0.3f, KeywordType.NEGATIVE)
        ),
        listOf("white", "cream", "art", "pattern")
    ),
    ESPRESSO(
        "Espresso",
        listOf(
            KeywordWeight("espresso", 4.0f, KeywordType.PRIMARY),
            KeywordWeight("coffee", 1.5f, KeywordType.PRIMARY),
            KeywordWeight("black coffee", 2.5f, KeywordType.PRIMARY),
            KeywordWeight("caffeine", 1.5f, KeywordType.SECONDARY),
            KeywordWeight("cup", 0.3f, KeywordType.SECONDARY),
            KeywordWeight("small cup", 1.8f, KeywordType.SECONDARY),
            KeywordWeight("strong", 1.5f, KeywordType.SECONDARY),
            KeywordWeight("juice", -2.0f, KeywordType.NEGATIVE),
            KeywordWeight("wine", -2.0f, KeywordType.NEGATIVE),
            KeywordWeight("milk", -2.0f, KeywordType.NEGATIVE),
            KeywordWeight("foam", -2.5f, KeywordType.NEGATIVE),
            KeywordWeight("cream", -2.0f, KeywordType.NEGATIVE),
            KeywordWeight("chocolate", -2.0f, KeywordType.NEGATIVE),
            KeywordWeight("tea", -3.0f, KeywordType.NEGATIVE)
        ),
        listOf("dark", "black", "brown", "small", "concentrated")
    ),
    AMERICANO(
        "Americano",
        listOf(
            KeywordWeight("americano", 3.0f, KeywordType.PRIMARY),
            KeywordWeight("black coffee", 2.5f, KeywordType.PRIMARY),
            KeywordWeight("coffee", 1.5f, KeywordType.SECONDARY),
            KeywordWeight("caffeine", 1.0f, KeywordType.SECONDARY),
            KeywordWeight("cup", 0.5f, KeywordType.SECONDARY),
            KeywordWeight("milk", -2.0f, KeywordType.NEGATIVE),
            KeywordWeight("foam", -2.0f, KeywordType.NEGATIVE)
        ),
        listOf("dark", "black", "large cup")
    ),
    MACCHIATO(
        "Macchiato",
        listOf(
            KeywordWeight("macchiato", 3.0f, KeywordType.PRIMARY),
            KeywordWeight("espresso macchiato", 3.0f, KeywordType.PRIMARY),
            KeywordWeight("coffee", 1.0f, KeywordType.SECONDARY),
            KeywordWeight("milk", 1.0f, KeywordType.SECONDARY),
            KeywordWeight("foam", 1.5f, KeywordType.SECONDARY),
            KeywordWeight("cup", 0.5f, KeywordType.SECONDARY)
        ),
        listOf("dark", "spot", "small")
    ),
    MOCHA(
        "Mocha",
        listOf(
            KeywordWeight("mocha", 3.0f, KeywordType.PRIMARY),
            KeywordWeight("chocolate", 2.5f, KeywordType.PRIMARY),
            KeywordWeight("cocoa", 2.0f, KeywordType.PRIMARY),
            KeywordWeight("coffee", 1.0f, KeywordType.SECONDARY),
            KeywordWeight("milk", 1.0f, KeywordType.SECONDARY),
            KeywordWeight("cream", 1.5f, KeywordType.SECONDARY),
            KeywordWeight("whipped cream", 2.0f, KeywordType.SECONDARY)
        ),
        listOf("brown", "chocolate", "cream")
    ),
    FLAT_WHITE(
        "Flat White",
        listOf(
            KeywordWeight("flat white", 3.0f, KeywordType.PRIMARY),
            KeywordWeight("coffee", 1.0f, KeywordType.SECONDARY),
            KeywordWeight("milk", 2.0f, KeywordType.SECONDARY),
            KeywordWeight("microfoam", 2.5f, KeywordType.PRIMARY),
            KeywordWeight("velvety", 1.5f, KeywordType.SECONDARY),
            KeywordWeight("foam", -1.0f, KeywordType.NEGATIVE)
        ),
        listOf("white", "smooth", "velvety")
    ),
    TEA(
        "Tea",
        listOf(
            KeywordWeight("tea", 4.0f, KeywordType.PRIMARY),
            KeywordWeight("tea cup", 3.5f, KeywordType.PRIMARY),
            KeywordWeight("herbal tea", 3.5f, KeywordType.PRIMARY),
            KeywordWeight("green tea", 3.5f, KeywordType.PRIMARY),
            KeywordWeight("black tea", 3.5f, KeywordType.PRIMARY),
            KeywordWeight("teapot", 3.0f, KeywordType.PRIMARY),
            KeywordWeight("teacup", 3.0f, KeywordType.PRIMARY),
            KeywordWeight("juice", 1.5f, KeywordType.SECONDARY),
            KeywordWeight("wine", 1.0f, KeywordType.SECONDARY),
            KeywordWeight("beverage", 0.2f, KeywordType.SECONDARY),
            KeywordWeight("drink", 0.2f, KeywordType.SECONDARY),
            KeywordWeight("coffee", -3.0f, KeywordType.NEGATIVE),
            KeywordWeight("espresso", -3.0f, KeywordType.NEGATIVE),
            KeywordWeight("cappuccino", -3.5f, KeywordType.NEGATIVE),
            KeywordWeight("latte", -3.5f, KeywordType.NEGATIVE),
            KeywordWeight("chocolate", -3.5f, KeywordType.NEGATIVE),
            KeywordWeight("cocoa", -3.5f, KeywordType.NEGATIVE)
        ),
        listOf("amber", "golden", "clear", "transparent", "yellow", "green", "red", "orange")
    ),
    HOT_CHOCOLATE(
        "Hot Chocolate",
        listOf(
            KeywordWeight("hot chocolate", 4.0f, KeywordType.PRIMARY),
            KeywordWeight("chocolate", 3.0f, KeywordType.PRIMARY),
            KeywordWeight("cocoa", 3.0f, KeywordType.PRIMARY),
            KeywordWeight("chocolate drink", 3.5f, KeywordType.PRIMARY),
            KeywordWeight("whipped cream", 2.5f, KeywordType.SECONDARY),
            KeywordWeight("marshmallow", 2.5f, KeywordType.SECONDARY),
            KeywordWeight("cream", 1.5f, KeywordType.SECONDARY),
            KeywordWeight("sweet", 1.0f, KeywordType.SECONDARY),
            KeywordWeight("coffee", -2.0f, KeywordType.NEGATIVE),
            KeywordWeight("tea", -3.0f, KeywordType.NEGATIVE)
        ),
        listOf("brown", "chocolate", "cream", "marshmallow", "white")
    ),
    ICED_COFFEE(
        "Iced Coffee",
        listOf(
            KeywordWeight("iced coffee", 3.0f, KeywordType.PRIMARY),
            KeywordWeight("cold brew", 3.0f, KeywordType.PRIMARY),
            KeywordWeight("ice", 2.5f, KeywordType.PRIMARY),
            KeywordWeight("iced", 2.5f, KeywordType.PRIMARY),
            KeywordWeight("coffee", 1.5f, KeywordType.SECONDARY),
            KeywordWeight("cold", 2.0f, KeywordType.SECONDARY),
            KeywordWeight("glass", 1.0f, KeywordType.SECONDARY)
        ),
        listOf("ice", "cold", "glass", "straw")
    ),
    FRAPPE(
        "Frappe",
        listOf(
            KeywordWeight("frappe", 3.0f, KeywordType.PRIMARY),
            KeywordWeight("frappuccino", 3.0f, KeywordType.PRIMARY),
            KeywordWeight("blended", 2.5f, KeywordType.PRIMARY),
            KeywordWeight("ice", 2.0f, KeywordType.SECONDARY),
            KeywordWeight("coffee", 1.0f, KeywordType.SECONDARY),
            KeywordWeight("whipped cream", 2.0f, KeywordType.SECONDARY),
            KeywordWeight("smoothie", 1.5f, KeywordType.SECONDARY)
        ),
        listOf("ice", "blended", "cream", "straw")
    ),
    UNKNOWN(
        "Unknown Drink",
        listOf(),
        emptyList()
    );

    companion object {
        fun fromLabels(labels: List<DetectedLabel>): Pair<DrinkType, Float> {
            if (labels.isEmpty()) {
                return UNKNOWN to 0f
            }

            val labelTexts = labels.map { it.text.lowercase() }

            val hasCappuccinoLabel = labels.any {
                it.text.lowercase() == "cappuccino" && it.confidence < 0.7f
            }
            val hasCoffeeLabel = labels.any {
                it.text.lowercase() == "coffee" && it.confidence > 0.7f
            }
            val hasChocolateHints = labelTexts.any {
                it.contains("chocolate") || it.contains("cocoa") || it.contains("sweet")
            }

            if (hasCappuccinoLabel && !hasCoffeeLabel && hasChocolateHints) {
                android.util.Log.d("DrinkType", "Possible hot chocolate detected (cappuccino-like with chocolate hints)")
            }

            val hasJuiceLabel = labels.any {
                it.text.lowercase() == "juice" && it.confidence > 0.5f
            }
            val hasWeakCoffeeLabel = labels.any {
                val text = it.text.lowercase()
                text == "coffee" && it.confidence < 0.8f && it.confidence > 0.5f
            }
            val hasWineLabel = labels.any {
                it.text.lowercase() == "wine" && it.confidence > 0.3f
            }

            val isPossiblyTea = (hasJuiceLabel || hasWineLabel) && hasWeakCoffeeLabel
            if (isPossiblyTea) {
                android.util.Log.d("DrinkType", "Possible tea detected (juice/wine + weak coffee combination)")
            }

            for (label in labels) {
                val labelLower = label.text.lowercase()

                for (drinkType in entries.filter { it != UNKNOWN }) {
                    if (labelLower == drinkType.displayName.lowercase() && label.confidence > 0.8f) {
                        android.util.Log.d("DrinkType", "Direct match found: ${drinkType.displayName} (confidence: ${label.confidence})")
                        return drinkType to label.confidence
                    }
                }

                if (labelLower == "cappuccino" && label.confidence > 0.5f && label.confidence <= 0.8f) {
                    val hasStrongCoffee = labels.any { it.text.lowercase() == "coffee" && it.confidence > 0.85f }
                    if (hasStrongCoffee) {
                        android.util.Log.d("DrinkType", "Medium confidence cappuccino with strong coffee confirmation")
                        return CAPPUCCINO to label.confidence
                    }
                }
            }

            val scores = mutableMapOf<DrinkType, Float>()

            for (drinkType in entries.filter { it != UNKNOWN }) {
                var totalScore = 0f
                var matchCount = 0
                var hasDirectMatch = false
                var hasPrimaryMatch = false

                for (keywordWeight in drinkType.keywords) {
                    for (label in labels) {
                        val labelLower = label.text.lowercase()
                        val keywordLower = keywordWeight.keyword.lowercase()
                        val exactMatch = labelLower == keywordLower
                        val partialMatch = labelLower.contains(keywordLower) ||
                                keywordLower.contains(labelLower)

                        if (exactMatch || partialMatch) {
                            val weightMultiplier = when (keywordWeight.type) {
                                KeywordType.PRIMARY -> 1.0f
                                KeywordType.SECONDARY -> 0.5f
                                KeywordType.NEGATIVE -> -1.0f
                            }

                            val exactMatchBonus = if (exactMatch && keywordWeight.type == KeywordType.PRIMARY) 2.0f else 1.0f

                            val scoreContribution = label.confidence * keywordWeight.weight * weightMultiplier * exactMatchBonus
                            totalScore += scoreContribution
                            matchCount++

                            if (exactMatch && keywordWeight.type == KeywordType.PRIMARY) {
                                hasDirectMatch = true
                            }

                            if (keywordWeight.type == KeywordType.PRIMARY && (exactMatch || partialMatch)) {
                                hasPrimaryMatch = true
                            }
                        }
                    }
                }
                val visualBonus = drinkType.visualFeatures.count { feature ->
                    labelTexts.any { it.contains(feature) }
                } * 0.3f
                val directMatchBonus = if (hasDirectMatch) 3.0f else 0f
                val teaHintBonus = if (drinkType == TEA && isPossiblyTea) 4.0f else 0f

                if (matchCount > 0 || visualBonus > 0 || teaHintBonus > 0) {
                    val finalScore = totalScore + visualBonus + directMatchBonus + teaHintBonus
                    if (drinkType == TEA) {
                        val hasStrongCoffeeIndicators = labels.any { label ->
                            val text = label.text.lowercase()
                            (text == "coffee" && label.confidence > 0.8f) ||
                                    (text == "espresso" && label.confidence > 0.5f) ||
                                    (text == "cappuccino" && label.confidence > 0.5f) ||
                                    (text == "latte" && label.confidence > 0.5f)
                        }

                        if (!hasPrimaryMatch && !isPossiblyTea && hasStrongCoffeeIndicators) {
                            android.util.Log.d("DrinkType", "TEA rejected: no PRIMARY keyword, no tea hints, and strong coffee indicators present")
                            continue
                        }

                        if (!hasPrimaryMatch && !isPossiblyTea) {
                            val penalizedScore = finalScore * 0.3f
                            scores[drinkType] = penalizedScore
                            android.util.Log.d("DrinkType", "${drinkType.displayName}: score=$penalizedScore (base=$totalScore, visual=$visualBonus, direct=$directMatchBonus, teaHint=$teaHintBonus, matches=$matchCount, primary=$hasPrimaryMatch, PENALIZED)")
                            continue
                        }
                    }

                    scores[drinkType] = finalScore
                    android.util.Log.d("DrinkType", "${drinkType.displayName}: score=$finalScore (base=$totalScore, visual=$visualBonus, direct=$directMatchBonus, teaHint=$teaHintBonus, matches=$matchCount, primary=$hasPrimaryMatch)")
                }
            }

            val bestMatch = scores.maxByOrNull { it.value }

            return if (bestMatch != null && bestMatch.value > 0.4f) {
                val confidence = (bestMatch.value / 8.0f).coerceIn(0f, 1f)
                android.util.Log.d("DrinkType", "Best match: ${bestMatch.key.displayName} with confidence $confidence")
                bestMatch.key to confidence
            } else {
                android.util.Log.d("DrinkType", "No confident match found, returning UNKNOWN")
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