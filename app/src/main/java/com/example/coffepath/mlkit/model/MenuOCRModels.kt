package com.example.coffepath.mlkit.model

import java.io.Serializable

data class MenuOCRResult(
    val fullText: String,
    val textBlocks: List<TextBlock>,
    val detectedItems: List<MenuItem>,
    val confidence: Float
) : Serializable {
    fun hasText(): Boolean = fullText.isNotBlank()
    fun hasMenuItems(): Boolean = detectedItems.isNotEmpty()
    fun getBlockCount(): Int = textBlocks.size
}

data class TextBlock(
    val text: String,
    val confidence: Float,
    val boundingBox: BoundingBox?
) : Serializable

data class BoundingBox(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
) : Serializable {
    fun width(): Int = right - left
    fun height(): Int = bottom - top
}

data class MenuItem(
    val name: String,
    val price: String?,
    val description: String?,
    val category: MenuCategory
) : Serializable {

    fun getFullDescription(): String {
        val parts = mutableListOf<String>()
        parts.add(name)
        if (!description.isNullOrBlank()) {
            parts.add(description)
        }
        if (!price.isNullOrBlank()) {
            parts.add(price)
        }
        return parts.joinToString(" - ")
    }
}

enum class MenuCategory(val displayName: String) {
    COFFEE("Coffee"),
    TEA("Tea"),
    DESSERT("Dessert"),
    FOOD("Food"),
    DRINK("Drink"),
    SPECIAL("Special"),
    UNKNOWN("Other");

    companion object {
        fun fromItemName(name: String): MenuCategory {
            val lowerName = name.lowercase()

            return when {
                lowerName.contains("coffee") ||
                        lowerName.contains("espresso") ||
                        lowerName.contains("cappuccino") ||
                        lowerName.contains("latte") ||
                        lowerName.contains("americano") ||
                        lowerName.contains("macchiato") ||
                        lowerName.contains("mocha") -> COFFEE

                lowerName.contains("tea") ||
                        lowerName.contains("chai") -> TEA

                lowerName.contains("cake") ||
                        lowerName.contains("cookie") ||
                        lowerName.contains("brownie") ||
                        lowerName.contains("muffin") ||
                        lowerName.contains("dessert") -> DESSERT

                lowerName.contains("sandwich") ||
                        lowerName.contains("salad") ||
                        lowerName.contains("soup") ||
                        lowerName.contains("breakfast") ||
                        lowerName.contains("lunch") -> FOOD

                lowerName.contains("juice") ||
                        lowerName.contains("smoothie") ||
                        lowerName.contains("water") ||
                        lowerName.contains("soda") -> DRINK

                else -> UNKNOWN
            }
        }
    }
}

object MenuParser {

    private val priceRegex = Regex("""(\d+[.,]\d{0,2})\s*(Kč|CZK|€|EUR|\$|USD)?""", RegexOption.IGNORE_CASE)
    private val menuItemRegex = Regex("""^[A-ZÁ-Ž].*""")

    fun parseMenu(fullText: String, textBlocks: List<TextBlock>): List<MenuItem> {
        val items = mutableListOf<MenuItem>()
        val lines = fullText.split("\n").filter { it.isNotBlank() }

        var i = 0
        while (i < lines.size) {
            val line = lines[i].trim()

            if (line.length < 3 || isHeader(line)) {
                i++
                continue
            }
            if (menuItemRegex.matches(line)) {
                val price = extractPrice(line)
                val name = removePriceFromName(line, price)

                val description = if (i + 1 < lines.size &&
                    !menuItemRegex.matches(lines[i + 1]) &&
                    !containsPrice(lines[i + 1])) {
                    i++
                    lines[i].trim()
                } else {
                    null
                }

                val category = MenuCategory.fromItemName(name)

                items.add(MenuItem(
                    name = name,
                    price = price,
                    description = description,
                    category = category
                ))
            }
            i++
        }
        return items
    }

    private fun extractPrice(text: String): String? {
        val match = priceRegex.find(text)
        return match?.value?.trim()
    }

    private fun removePriceFromName(text: String, price: String?): String {
        return if (price != null) {
            text.replace(price, "").trim()
        } else {
            text.trim()
        }
    }

    private fun containsPrice(text: String): Boolean {
        return priceRegex.containsMatchIn(text)
    }

    private fun isHeader(text: String): Boolean {
        val upperCount = text.count { it.isUpperCase() }
        val totalLetters = text.count { it.isLetter() }

        return if (totalLetters > 0) {
            (upperCount.toFloat() / totalLetters) > 0.7f
        } else {
            false
        }
    }
}