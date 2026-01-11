package com.example.coffepath.model

import org.junit.Assert.*
import org.junit.Test

class CafeFilterTest {

    @Test
    fun `default CafeFilter has no active filters`() {
        val filter = CafeFilter()
        val hasActive = filter.hasActiveFilters()
        assertFalse(hasActive)
    }

    @Test
    fun `CafeFilter with minRating has active filters`() {
        val filter = CafeFilter(minRating = 4.0f)
        val hasActive = filter.hasActiveFilters()
        assertTrue(hasActive)
    }

    @Test
    fun `CafeFilter with maxDistance has active filters`() {
        val filter = CafeFilter(maxDistance = 5.0f)
        val hasActive = filter.hasActiveFilters()
        assertTrue(hasActive)
    }

    @Test
    fun `CafeFilter with isOpenNow true has active filters`() {
        val filter = CafeFilter(isOpenNow = true)
        val hasActive = filter.hasActiveFilters()
        assertTrue(hasActive)
    }

    @Test
    fun `CafeFilter with hasReviews true has active filters`() {
        val filter = CafeFilter(hasReviews = true)
        val hasActive = filter.hasActiveFilters()
        assertTrue(hasActive)
    }

    @Test
    fun `CafeFilter with isFavorite true has active filters`() {
        val filter = CafeFilter(isFavorite = true)
        val hasActive = filter.hasActiveFilters()
        assertTrue(hasActive)
    }

    @Test
    fun `getActiveFiltersCount returns 0 for default filter`() {
        val filter = CafeFilter()
        val count = filter.getActiveFiltersCount()
        assertEquals(0, count)
    }

    @Test
    fun `getActiveFiltersCount returns 1 for single filter`() {
        val filter = CafeFilter(minRating = 4.0f)
        val count = filter.getActiveFiltersCount()
        assertEquals(1, count)
    }

    @Test
    fun `getActiveFiltersCount returns correct count for multiple filters`() {
        val filter = CafeFilter(
            minRating = 4.0f,
            maxDistance = 5.0f,
            isOpenNow = true
        )
        val count = filter.getActiveFiltersCount()
        assertEquals(3, count)
    }

    @Test
    fun `getActiveFiltersCount returns 5 when all filters active`() {
        val filter = CafeFilter(
            minRating = 4.0f,
            maxDistance = 5.0f,
            isOpenNow = true,
            hasReviews = true,
            isFavorite = true
        )
        val count = filter.getActiveFiltersCount()
        assertEquals(5, count)
    }

    @Test
    fun `clear returns default filter`() {
        val filter = CafeFilter(
            minRating = 4.0f,
            maxDistance = 5.0f,
            isOpenNow = true
        )
        val cleared = filter.clear()
        assertFalse(cleared.hasActiveFilters())
        assertEquals(0, cleared.getActiveFiltersCount())
        assertNull(cleared.minRating)
        assertNull(cleared.maxDistance)
        assertFalse(cleared.isOpenNow)
        assertFalse(cleared.hasReviews)
        assertFalse(cleared.isFavorite)
    }

    @Test
    fun `CafeFilter is serializable`() {
        val filter = CafeFilter(minRating = 4.0f)
        assertTrue(filter is java.io.Serializable)
    }

    @Test
    fun `CafeFilter with null minRating counts as no filter`() {
        val filter = CafeFilter(minRating = null)
        val count = filter.getActiveFiltersCount()
        assertEquals(0, count)
    }

    @Test
    fun `CafeFilter with null maxDistance counts as no filter`() {
        val filter = CafeFilter(maxDistance = null)
        val count = filter.getActiveFiltersCount()
        assertEquals(0, count)
    }

    @Test
    fun `copy constructor works correctly`() {
        val original = CafeFilter(minRating = 4.0f, hasReviews = true)
        val copy = original.copy(minRating = 5.0f)
        assertEquals(5.0f, copy.minRating)
        assertTrue(copy.hasReviews) // Should keep other properties
        assertNotEquals(original.minRating, copy.minRating)
    }
}