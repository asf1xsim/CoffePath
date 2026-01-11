package com.example.coffepath.model

import com.example.coffepath.mlkit.model.DetectedLabel
import com.example.coffepath.mlkit.model.DrinkType
import org.junit.Assert.*
import org.junit.Test

class DrinkTypeTest {

    @Test
    fun `fromLabels returns CAPPUCCINO when cappuccino label present with high confidence`() {
        val labels = listOf(
            DetectedLabel("Cappuccino", 0.9f, 0),
            DetectedLabel("Coffee", 0.8f, 1),
            DetectedLabel("Cup", 0.7f, 2)
        )
        val (drinkType, confidence) = DrinkType.fromLabels(labels)
        assertEquals(DrinkType.CAPPUCCINO, drinkType)
        assertTrue("Confidence should be > 0", confidence > 0f)
    }

    @Test
    fun `fromLabels returns ESPRESSO when espresso and black coffee labels present`() {
        val labels = listOf(
            DetectedLabel("Espresso", 0.85f, 0),
            DetectedLabel("Black coffee", 0.75f, 1),
            DetectedLabel("Coffee", 0.7f, 2)
        )
        val (drinkType, confidence) = DrinkType.fromLabels(labels)
        assertEquals(DrinkType.ESPRESSO, drinkType)
        assertTrue("Confidence should be > 0", confidence > 0f)
    }

    @Test
    fun `fromLabels returns TEA when tea label present`() {
        val labels = listOf(
            DetectedLabel("Tea", 0.9f, 0),
            DetectedLabel("Tea cup", 0.8f, 1),
            DetectedLabel("Drink", 0.5f, 2)
        )
        val (drinkType, confidence) = DrinkType.fromLabels(labels)
        assertEquals(DrinkType.TEA, drinkType)
        assertTrue("Confidence should be > 0", confidence > 0f)
    }

    @Test
    fun `fromLabels returns LATTE when latte and milk labels present`() {
        val labels = listOf(
            DetectedLabel("Latte", 0.85f, 0),
            DetectedLabel("Milk", 0.8f, 1),
            DetectedLabel("Coffee", 0.6f, 2)
        )
        val (drinkType, confidence) = DrinkType.fromLabels(labels)
        assertEquals(DrinkType.LATTE, drinkType)
        assertTrue("Confidence should be > 0", confidence > 0f)
    }

    @Test
    fun `fromLabels returns HOT_CHOCOLATE when chocolate labels present`() {
        val labels = listOf(
            DetectedLabel("Chocolate", 0.9f, 0),
            DetectedLabel("Hot chocolate", 0.85f, 1),
            DetectedLabel("Cream", 0.7f, 2)
        )
        val (drinkType, confidence) = DrinkType.fromLabels(labels)
        assertEquals(DrinkType.HOT_CHOCOLATE, drinkType)
        assertTrue("Confidence should be > 0", confidence > 0f)
    }

    @Test
    fun `fromLabels returns UNKNOWN when no relevant labels present`() {
        val labels = listOf(
            DetectedLabel("Table", 0.9f, 0),
            DetectedLabel("Wood", 0.8f, 1),
            DetectedLabel("Room", 0.7f, 2)
        )
        val (drinkType, confidence) = DrinkType.fromLabels(labels)
        assertEquals(DrinkType.UNKNOWN, drinkType)
        assertEquals(0f, confidence, 0.01f)
    }

    @Test
    fun `fromLabels returns UNKNOWN when labels list is empty`() {
        val labels = emptyList<DetectedLabel>()
        val (drinkType, confidence) = DrinkType.fromLabels(labels)
        assertEquals(DrinkType.UNKNOWN, drinkType)
        assertEquals(0f, confidence, 0.01f)
    }

    @Test
    fun `fromString returns correct DrinkType for valid name`() {
        assertEquals(DrinkType.CAPPUCCINO, DrinkType.fromString("Cappuccino"))
        assertEquals(DrinkType.ESPRESSO, DrinkType.fromString("Espresso"))
        assertEquals(DrinkType.LATTE, DrinkType.fromString("Latte"))
        assertEquals(DrinkType.TEA, DrinkType.fromString("Tea"))
    }

    @Test
    fun `fromString is case insensitive`() {
        assertEquals(DrinkType.CAPPUCCINO, DrinkType.fromString("cappuccino"))
        assertEquals(DrinkType.CAPPUCCINO, DrinkType.fromString("CAPPUCCINO"))
        assertEquals(DrinkType.CAPPUCCINO, DrinkType.fromString("CaPpUcCiNo"))
    }

    @Test
    fun `fromString returns UNKNOWN for invalid name`() {
        assertEquals(DrinkType.UNKNOWN, DrinkType.fromString("InvalidDrink"))
        assertEquals(DrinkType.UNKNOWN, DrinkType.fromString(""))
        assertEquals(DrinkType.UNKNOWN, DrinkType.fromString("Water"))
    }

    @Test
    fun `all DrinkTypes have non-empty displayName`() {
        DrinkType.entries.forEach { drinkType ->
            assertTrue(
                "DrinkType ${drinkType.name} should have non-empty displayName",
                drinkType.displayName.isNotBlank()
            )
        }
    }

    @Test
    fun `CAPPUCCINO has foam related keywords`() {
        val foamKeywords = DrinkType.CAPPUCCINO.keywords.filter {
            it.keyword.contains("foam", ignoreCase = true)
        }
        assertTrue("Cappuccino should have foam keywords", foamKeywords.isNotEmpty())
    }

    @Test
    fun `ESPRESSO has negative weights for milk`() {
        val milkKeywords = DrinkType.ESPRESSO.keywords.filter {
            it.keyword.contains("milk", ignoreCase = true)
        }
        assertTrue("Espresso should have milk in keywords", milkKeywords.isNotEmpty())
        assertTrue(
            "Espresso milk keyword should have negative weight",
            milkKeywords.any { it.weight < 0 }
        )
    }
}