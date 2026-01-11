package com.example.coffepath.model

import org.junit.Assert.*
import org.junit.Test

class UserProfileTest {

    @Test
    fun `getInitials returns two letters for full name`() {
        val profile = UserProfile(
            userId = "user1",
            name = "John Doe",
            email = null,
            photoUri = null
        )
        val initials = profile.getInitials()
        assertEquals("JD", initials)
    }

    @Test
    fun `getInitials returns first two letters for single name`() {
        val profile = UserProfile(
            userId = "user1",
            name = "John",
            email = null,
            photoUri = null
        )
        val initials = profile.getInitials()
        assertEquals("JO", initials)
    }

    @Test
    fun `getInitials handles multiple spaces in name`() {
        val profile = UserProfile(
            userId = "user1",
            name = "John   Michael   Doe",
            email = null,
            photoUri = null
        )
        val initials = profile.getInitials()
        assertEquals("JM", initials)
    }

    @Test
    fun `getInitials is uppercase`() {
        val profile = UserProfile(
            userId = "user1",
            name = "john doe",
            email = null,
            photoUri = null
        )
        val initials = profile.getInitials()
        assertEquals("JD", initials)
        assertTrue(initials.all { it.isUpperCase() })
    }

    @Test
    fun `getInitials returns U for empty name`() {
        val profile = UserProfile(
            userId = "user1",
            name = "",
            email = null,
            photoUri = null
        )
        val initials = profile.getInitials()
        assertEquals("U", initials)
    }

    @Test
    fun `getInitials handles name with leading and trailing spaces`() {
        val profile = UserProfile(
            userId = "user1",
            name = "  John Doe  ",
            email = null,
            photoUri = null
        )
        val initials = profile.getInitials()
        assertEquals("JD", initials)
    }

    @Test
    fun `default values are set correctly`() {
        val profile = UserProfile(
            userId = "user1",
            name = "Test User"
        )
        assertNull(profile.email)
        assertNull(profile.photoUri)
        assertEquals(0, profile.reviewsCount)
        assertEquals(0, profile.favoritesCount)
        assertEquals(0, profile.visitedCafesCount)
        assertTrue(profile.createdAt > 0)
        assertTrue(profile.updatedAt > 0)
    }

    @Test
    fun `UserProfile is serializable`() {
        val profile = UserProfile(
            userId = "user1",
            name = "Test User"
        )
        assertTrue(profile is java.io.Serializable)
    }

    @Test
    fun `all counters can be set to positive values`() {
        val profile = UserProfile(
            userId = "user1",
            name = "Test User",
            reviewsCount = 10,
            favoritesCount = 5,
            visitedCafesCount = 15
        )
        assertEquals(10, profile.reviewsCount)
        assertEquals(5, profile.favoritesCount)
        assertEquals(15, profile.visitedCafesCount)
    }

    @Test
    fun `copy works correctly for name`() {
        val original = UserProfile(
            userId = "user1",
            name = "John Doe"
        )
        val updated = original.copy(name = "Jane Doe")
        assertEquals("Jane Doe", updated.name)
        assertEquals("user1", updated.userId)
    }

    @Test
    fun `copy works correctly for counters`() {
        val original = UserProfile(
            userId = "user1",
            name = "Test User",
            reviewsCount = 5
        )
        val updated = original.copy(reviewsCount = 10)
        assertEquals(10, updated.reviewsCount)
        assertEquals("Test User", updated.name)
    }

    @Test
    fun `timestamps are reasonable values`() {
        val profile = UserProfile(
            userId = "user1",
            name = "Test User"
        )
        val currentTime = System.currentTimeMillis()
        assertTrue(profile.createdAt <= currentTime)
        assertTrue(profile.updatedAt <= currentTime)
        assertTrue(profile.createdAt > 0)
        assertTrue(profile.updatedAt > 0)
    }

    @Test
    fun `getInitials handles single letter name`() {
        val profile = UserProfile(
            userId = "user1",
            name = "J"
        )
        val initials = profile.getInitials()
        assertEquals("J", initials)
    }
}