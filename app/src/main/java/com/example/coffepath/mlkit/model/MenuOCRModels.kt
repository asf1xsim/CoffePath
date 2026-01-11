package com.example.coffepath.mlkit.model

import android.util.Log
import kotlinx.serialization.Serializable
import java.io.Serializable as JavaSerializable

@Serializable
data class MenuOCRResult(
    val fullText: String,
    val textBlocks: List<TextBlock>,
    val detectedItems: List<MenuItem>,
    val confidence: Float
) : JavaSerializable {
    fun hasText(): Boolean = fullText.isNotBlank()
    fun hasMenuItems(): Boolean = detectedItems.isNotEmpty()
    fun getBlockCount(): Int = textBlocks.size
}

@Serializable
data class TextBlock(
    val text: String,
    val confidence: Float,
    val boundingBox: BoundingBox?
) : JavaSerializable

@Serializable
data class BoundingBox(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
) : JavaSerializable {
    fun width(): Int = right - left
    fun height(): Int = bottom - top
}

@Serializable
data class MenuItem(
    val name: String,
    val price: String?,
    val description: String?,
    val category: MenuCategory
) : JavaSerializable {

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
@Serializable
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
                        lowerName.contains("mocha") ||
                        lowerName.contains("ristretto") ||
                        lowerName.contains("doppio") ||
                        lowerName.contains("flat white") ||
                        lowerName.contains("káv") -> COFFEE

                lowerName.contains("tea") ||
                        lowerName.contains("chai") ||
                        lowerName.contains("čaj") -> TEA

                lowerName.contains("cake") ||
                        lowerName.contains("cookie") ||
                        lowerName.contains("brownie") ||
                        lowerName.contains("muffin") ||
                        lowerName.contains("dessert") ||
                        lowerName.contains("dezert") ||
                        lowerName.contains("dort") -> DESSERT

                lowerName.contains("sandwich") ||
                        lowerName.contains("salad") ||
                        lowerName.contains("soup") ||
                        lowerName.contains("breakfast") ||
                        lowerName.contains("lunch") ||
                        lowerName.contains("sendvič") ||
                        lowerName.contains("polévka") -> FOOD

                lowerName.contains("juice") ||
                        lowerName.contains("smoothie") ||
                        lowerName.contains("water") ||
                        lowerName.contains("soda") ||
                        lowerName.contains("šťáva") ||
                        lowerName.contains("voda") -> DRINK

                else -> UNKNOWN
            }
        }
    }
}

object MenuParser {
    private val priceRegex = Regex("""(\d+[.,]?\d{0,2})\s*(Kč|KČ|CZK|kč|€|EUR|\$|USD)?""", RegexOption.IGNORE_CASE)
    private val numberOnlyRegex = Regex("""^\d+$""")
    private val menuItemRegex = Regex("""^[A-ZÁ-Ž].*""")
    fun parseMenu(fullText: String, textBlocks: List<TextBlock>): List<MenuItem> {
        val items = mutableListOf<MenuItem>()
        val lines = fullText.split("\n").map { it.trim() }.filter { it.isNotBlank() }

        Log.d("ImprovedMenuParser", "=== Starting parsing ===")
        Log.d("ImprovedMenuParser", "Total lines: ${lines.size}")

        var i = 0
        while (i < lines.size) {
            val line = lines[i]

            Log.d("ImprovedMenuParser", "Line $i: '$line'")
            if (line.length < 3 || isHeader(line)) {
                Log.d("ImprovedMenuParser", "  -> Skipped (header or too short)")
                i++
                continue
            }
            if (menuItemRegex.matches(line)) {
                Log.d("ImprovedMenuParser", "  -> Potential menu item detected")

                var price = extractPriceFromLine(line)
                var name = line

                if (price != null) {
                    name = removePriceFromName(line, price)
                    Log.d("ImprovedMenuParser", "  -> Price found in same line: $price")
                } else {
                    if (i + 1 < lines.size) {
                        val nextLine = lines[i + 1].trim()
                        if (numberOnlyRegex.matches(nextLine)) {
                            price = nextLine
                            i++
                            Log.d("ImprovedMenuParser", "  -> Price found on next line: $price")
                        }
                    }
                }
                name = cleanName(name)

                var description: String? = null
                if (i + 1 < lines.size) {
                    val nextLine = lines[i + 1].trim()
                    if (!menuItemRegex.matches(nextLine) &&
                        !numberOnlyRegex.matches(nextLine) &&
                        !containsOnlyPrice(nextLine) &&
                        nextLine.length > 5) {
                        description = nextLine
                        i++
                        Log.d("ImprovedMenuParser", "  -> Description found: $description")
                    }
                }

                val category = MenuCategory.fromItemName(name)

                val menuItem = MenuItem(
                    name = name,
                    price = if (price != null) formatPrice(price) else null,
                    description = description,
                    category = category
                )

                items.add(menuItem)
                Log.d("ImprovedMenuParser", "  -> Added item: ${menuItem.name} - ${menuItem.price}")
            }
            i++
        }

        Log.d("ImprovedMenuParser", "=== Parsing complete: ${items.size} items found ===")
        return items
    }

    private fun extractPriceFromLine(text: String): String? {
        val match = priceRegex.find(text)
        if (match != null) {
            return match.value.trim()
        }
        val numbers = Regex("""\d+""").findAll(text).toList()
        return numbers.lastOrNull()?.value
    }

    private fun removePriceFromName(text: String, price: String?): String {
        var result = text
        if (price != null) {
            result = result.replace(price, "")
            result = result.replace(Regex("""(Kč|KČ|CZK|kč|€|EUR|\$|USD)""", RegexOption.IGNORE_CASE), "")
        }
        return result.trim()
    }

    private fun cleanName(name: String): String {
        return name
            .replace(Regex("""\.{2,}"""), "")
            .replace(Regex("""\s{2,}"""), " ")
            .trim()
    }

    private fun formatPrice(price: String): String {
        if (price.matches(Regex(""".*[A-Za-zČčÁáÉéÍíÓóÚúŮůÝýĎďŤťŇňŘřŠšŽž€\$].*"""))) {
            return price
        }
        return "$price Kč"
    }

    private fun containsOnlyPrice(text: String): Boolean {
        val trimmed = text.trim()
        return priceRegex.matches(trimmed) || numberOnlyRegex.matches(trimmed)
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

fun parseMenuImproved(fullText: String, textBlocks: List<TextBlock>): List<MenuItem> {
    return MenuParser.parseMenu(fullText, textBlocks)
}