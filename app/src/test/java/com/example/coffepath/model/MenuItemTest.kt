package com.example.coffepath.model

import com.example.coffepath.mlkit.model.MenuCategory
import com.example.coffepath.mlkit.model.MenuItem
import org.junit.Assert.*
import org.junit.Test

class MenuItemTest {

    @Test
    fun `getFullDescription returns name only when price and description are null`() {
        val item = MenuItem(
            name = "Cappuccino",
            price = null,
            description = null,
            category = MenuCategory.COFFEE
        )
        val result = item.getFullDescription()
        assertEquals("Cappuccino", result)
    }

    @Test
    fun `getFullDescription includes price when present`() {
        val item = MenuItem(
            name = "Cappuccino",
            price = "50 Kč",
            description = null,
            category = MenuCategory.COFFEE
        )
        val result = item.getFullDescription()
        assertTrue(result.contains("Cappuccino"))
        assertTrue(result.contains("50 Kč"))
        assertTrue(result.contains("-"))
    }

    @Test
    fun `getFullDescription includes description when present`() {
        val item = MenuItem(
            name = "Cappuccino",
            price = null,
            description = "With extra foam",
            category = MenuCategory.COFFEE
        )
        val result = item.getFullDescription()
        assertTrue(result.contains("Cappuccino"))
        assertTrue(result.contains("With extra foam"))
        assertTrue(result.contains("-"))
    }

    @Test
    fun `getFullDescription includes all parts when all present`() {
        val item = MenuItem(
            name = "Cappuccino",
            price = "50 Kč",
            description = "With extra foam",
            category = MenuCategory.COFFEE
        )
        val result = item.getFullDescription()
        assertTrue(result.contains("Cappuccino"))
        assertTrue(result.contains("50 Kč"))
        assertTrue(result.contains("With extra foam"))
        assertEquals(2, result.count { it == '-' })
    }

    @Test
    fun `MenuCategory fromItemName returns COFFEE for coffee related names`() {
        assertEquals(MenuCategory.COFFEE, MenuCategory.fromItemName("Cappuccino"))
        assertEquals(MenuCategory.COFFEE, MenuCategory.fromItemName("Espresso"))
        assertEquals(MenuCategory.COFFEE, MenuCategory.fromItemName("Latte"))
        assertEquals(MenuCategory.COFFEE, MenuCategory.fromItemName("Black Coffee"))
    }

    @Test
    fun `MenuCategory fromItemName returns TEA for tea related names`() {
        assertEquals(MenuCategory.TEA, MenuCategory.fromItemName("Green Tea"))
        assertEquals(MenuCategory.TEA, MenuCategory.fromItemName("Black Tea"))
        assertEquals(MenuCategory.TEA, MenuCategory.fromItemName("Chai"))
        assertEquals(MenuCategory.TEA, MenuCategory.fromItemName("Tea"))
    }

    @Test
    fun `MenuCategory fromItemName returns DESSERT for dessert related names`() {
        assertEquals(MenuCategory.DESSERT, MenuCategory.fromItemName("Chocolate Cake"))
        assertEquals(MenuCategory.DESSERT, MenuCategory.fromItemName("Cookie"))
        assertEquals(MenuCategory.DESSERT, MenuCategory.fromItemName("Brownie"))
        assertEquals(MenuCategory.DESSERT, MenuCategory.fromItemName("Muffin"))
    }

    @Test
    fun `MenuCategory fromItemName returns FOOD for food related names`() {
        assertEquals(MenuCategory.FOOD, MenuCategory.fromItemName("Sandwich"))
        assertEquals(MenuCategory.FOOD, MenuCategory.fromItemName("Salad"))
        assertEquals(MenuCategory.FOOD, MenuCategory.fromItemName("Soup"))
    }

    @Test
    fun `MenuCategory fromItemName returns DRINK for drink related names`() {
        assertEquals(MenuCategory.DRINK, MenuCategory.fromItemName("Orange Juice"))
        assertEquals(MenuCategory.DRINK, MenuCategory.fromItemName("Smoothie"))
        assertEquals(MenuCategory.DRINK, MenuCategory.fromItemName("Water"))
    }

    @Test
    fun `MenuCategory fromItemName returns UNKNOWN for unrecognized names`() {
        assertEquals(MenuCategory.UNKNOWN, MenuCategory.fromItemName("Something Random"))
        assertEquals(MenuCategory.UNKNOWN, MenuCategory.fromItemName(""))
        assertEquals(MenuCategory.UNKNOWN, MenuCategory.fromItemName("XYZ"))
    }

    @Test
    fun `MenuCategory fromItemName is case insensitive`() {
        assertEquals(MenuCategory.COFFEE, MenuCategory.fromItemName("CAPPUCCINO"))
        assertEquals(MenuCategory.COFFEE, MenuCategory.fromItemName("cappuccino"))
        assertEquals(MenuCategory.TEA, MenuCategory.fromItemName("TEA"))
        assertEquals(MenuCategory.TEA, MenuCategory.fromItemName("tea"))
    }

    @Test
    fun `MenuItem with blank strings behaves correctly`() {
        val item = MenuItem(
            name = "Espresso",
            price = "   ",
            description = "   ",
            category = MenuCategory.COFFEE
        )
        val result = item.getFullDescription()
        assertTrue(result.contains("Espresso"))
    }

    @Test
    fun `all MenuCategory values have non-empty displayName`() {
        MenuCategory.entries.forEach { category ->
            assertTrue(
                "Category ${category.name} should have non-empty displayName",
                category.displayName.isNotBlank()
            )
        }
    }
}