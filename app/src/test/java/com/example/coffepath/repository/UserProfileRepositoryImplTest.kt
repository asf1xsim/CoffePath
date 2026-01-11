package com.example.coffepath.repository

import app.cash.turbine.test
import com.example.coffepath.database.UserProfileDao
import com.example.coffepath.model.UserProfile
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UserProfileRepositoryImplTest : BaseRepositoryTest() {

    private lateinit var userProfileDao: UserProfileDao
    private lateinit var preferencesRepository: PreferencesRepository
    private lateinit var repository: UserProfileRepositoryImpl

    private val testProfile = UserProfile(
        userId = "user123",
        name = "Test User",
        email = "test@example.com",
        photoUri = null,
        reviewsCount = 5,
        favoritesCount = 3,
        visitedCafesCount = 10,
        createdAt = 1000000L,
        updatedAt = 2000000L
    )

    @Before
    override fun setup() {
        super.setup()
        userProfileDao = mockk(relaxed = true)
        preferencesRepository = mockk(relaxed = true)
        repository = UserProfileRepositoryImpl(userProfileDao, preferencesRepository)
    }

    @Test
    fun `getUserProfile returns flow from dao`() = runTest {
        every { userProfileDao.getUserProfileFlow("user123") } returns flowOf(testProfile)
        repository.getUserProfile("user123").test {
            assertEquals(testProfile, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `getUserProfile returns null flow when profile does not exist`() = runTest {
        every { userProfileDao.getUserProfileFlow("nonexistent") } returns flowOf(null)
        repository.getUserProfile("nonexistent").test {
            assertNull(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `getUserProfileSnapshot returns profile when exists`() = runTest {
        coEvery { userProfileDao.getUserProfile("user123") } returns testProfile
        val result = repository.getUserProfileSnapshot("user123")
        assertNotNull(result)
        assertEquals(testProfile.userId, result?.userId)
        assertEquals(testProfile.name, result?.name)
    }

    @Test
    fun `getUserProfileSnapshot returns null when profile does not exist`() = runTest {
        coEvery { userProfileDao.getUserProfile("nonexistent") } returns null
        val result = repository.getUserProfileSnapshot("nonexistent")
        assertNull(result)
    }

    @Test
    fun `createOrUpdateProfile inserts new profile when does not exist`() = runTest {
        val newProfile = testProfile.copy(userId = "newUser")
        coEvery { userProfileDao.getUserProfile("newUser") } returns null
        coEvery { userProfileDao.insert(newProfile) } returns Unit
        repository.createOrUpdateProfile(newProfile)
        coVerify(exactly = 1) { userProfileDao.insert(newProfile) }
        coVerify(exactly = 0) { userProfileDao.update(any()) }
    }

    @Test
    fun `createOrUpdateProfile updates existing profile preserving createdAt`() = runTest {
        val existingProfile = testProfile.copy(name = "Old Name", createdAt = 1000L)
        val updatedProfile = testProfile.copy(name = "New Name")
        coEvery { userProfileDao.getUserProfile("user123") } returns existingProfile
        val profileSlot = slot<UserProfile>()
        coEvery { userProfileDao.update(capture(profileSlot)) } returns Unit
        repository.createOrUpdateProfile(updatedProfile)
        coVerify(exactly = 1) { userProfileDao.update(any()) }
        coVerify(exactly = 0) { userProfileDao.insert(any()) }
        val capturedProfile = profileSlot.captured
        assertEquals("New Name", capturedProfile.name)
        assertEquals(1000L, capturedProfile.createdAt)
        assertTrue(capturedProfile.updatedAt > existingProfile.updatedAt)
    }

    @Test
    fun `updateProfileName updates dao and preferences when profile exists`() = runTest {
        coEvery { userProfileDao.getUserProfile("user123") } returns testProfile
        coEvery { userProfileDao.update(any()) } returns Unit
        coEvery { preferencesRepository.updateUserName("New Name") } returns Unit
        repository.updateProfileName("user123", "New Name")
        coVerify(exactly = 1) { userProfileDao.update(any()) }
        coVerify(exactly = 1) { preferencesRepository.updateUserName("New Name") }
    }

    @Test
    fun `updateProfileName only updates preferences when profile does not exist`() = runTest {
        coEvery { userProfileDao.getUserProfile("nonexistent") } returns null
        coEvery { preferencesRepository.updateUserName("New Name") } returns Unit
        repository.updateProfileName("nonexistent", "New Name")
        coVerify(exactly = 0) { userProfileDao.update(any()) }
        coVerify(exactly = 1) { preferencesRepository.updateUserName("New Name") }
    }

    @Test
    fun `updateProfileName preserves other profile fields`() = runTest {
        coEvery { userProfileDao.getUserProfile("user123") } returns testProfile
        val profileSlot = slot<UserProfile>()
        coEvery { userProfileDao.update(capture(profileSlot)) } returns Unit
        coEvery { preferencesRepository.updateUserName(any()) } returns Unit
        repository.updateProfileName("user123", "New Name")
        val capturedProfile = profileSlot.captured
        assertEquals("New Name", capturedProfile.name)
        assertEquals(testProfile.email, capturedProfile.email)
        assertEquals(testProfile.reviewsCount, capturedProfile.reviewsCount)
    }

    @Test
    fun `updateProfilePhoto updates photo uri when profile exists`() = runTest {
        val newPhotoUri = "file://photo.jpg"
        coEvery { userProfileDao.getUserProfile("user123") } returns testProfile
        val profileSlot = slot<UserProfile>()
        coEvery { userProfileDao.update(capture(profileSlot)) } returns Unit
        repository.updateProfilePhoto("user123", newPhotoUri)
        coVerify(exactly = 1) { userProfileDao.update(any()) }
        assertEquals(newPhotoUri, profileSlot.captured.photoUri)
    }

    @Test
    fun `updateProfilePhoto can set photo to null`() = runTest {
        coEvery { userProfileDao.getUserProfile("user123") } returns testProfile.copy(photoUri = "old.jpg")
        val profileSlot = slot<UserProfile>()
        coEvery { userProfileDao.update(capture(profileSlot)) } returns Unit
        repository.updateProfilePhoto("user123", null)
        assertNull(profileSlot.captured.photoUri)
    }

    @Test
    fun `updateProfilePhoto does nothing when profile does not exist`() = runTest {
        coEvery { userProfileDao.getUserProfile("nonexistent") } returns null
        repository.updateProfilePhoto("nonexistent", "photo.jpg")
        coVerify(exactly = 0) { userProfileDao.update(any()) }
    }

    @Test
    fun `incrementReviewsCount calls dao increment`() = runTest {
        coEvery { userProfileDao.incrementReviewsCount("user123", any()) } returns Unit
        repository.incrementReviewsCount("user123")
        coVerify(exactly = 1) { userProfileDao.incrementReviewsCount("user123", any()) }
    }

    @Test
    fun `incrementFavoritesCount calls dao increment`() = runTest {
        coEvery { userProfileDao.incrementFavoritesCount("user123") } returns Unit
        repository.incrementFavoritesCount("user123")
        coVerify(exactly = 1) { userProfileDao.incrementFavoritesCount("user123") }
    }

    @Test
    fun `incrementVisitedCafesCount calls dao increment`() = runTest {
        coEvery { userProfileDao.incrementVisitedCafesCount("user123") } returns Unit
        repository.incrementVisitedCafesCount("user123")
        coVerify(exactly = 1) { userProfileDao.incrementVisitedCafesCount("user123") }
    }

    @Test
    fun `decrementReviewsCount calls dao decrement`() = runTest {
        coEvery { userProfileDao.decrementReviewsCount("user123") } returns Unit
        repository.decrementReviewsCount("user123")
        coVerify(exactly = 1) { userProfileDao.decrementReviewsCount("user123") }
    }

    @Test
    fun `decrementFavoritesCount calls dao decrement`() = runTest {
        coEvery { userProfileDao.decrementFavoritesCount("user123") } returns Unit
        repository.decrementFavoritesCount("user123")
        coVerify(exactly = 1) { userProfileDao.decrementFavoritesCount("user123") }
    }

    @Test
    fun `updateStats updates all statistics when profile exists`() = runTest {
        coEvery { userProfileDao.getUserProfile("user123") } returns testProfile
        coEvery {
            userProfileDao.updateStats("user123", 10, 5, testProfile.visitedCafesCount)
        } returns Unit
        repository.updateStats("user123", reviewsCount = 10, favoritesCount = 5)
        coVerify(exactly = 1) {
            userProfileDao.updateStats("user123", 10, 5, testProfile.visitedCafesCount)
        }
    }

    @Test
    fun `updateStats does nothing when profile does not exist`() = runTest {
        coEvery { userProfileDao.getUserProfile("nonexistent") } returns null
        repository.updateStats("nonexistent", reviewsCount = 10, favoritesCount = 5)
        coVerify(exactly = 0) { userProfileDao.updateStats(any(), any(), any(), any()) }
    }

    @Test
    fun `updateStats preserves visitedCafesCount`() = runTest {
        val profileWithVisits = testProfile.copy(visitedCafesCount = 25)
        coEvery { userProfileDao.getUserProfile("user123") } returns profileWithVisits
        coEvery { userProfileDao.updateStats(any(), any(), any(), any()) } returns Unit
        repository.updateStats("user123", reviewsCount = 10, favoritesCount = 5)
        coVerify(exactly = 1) {
            userProfileDao.updateStats("user123", 10, 5, 25)
        }
    }

    @Test
    fun `getOrCreateCurrentUserProfile returns existing profile`() = runTest {
        every { preferencesRepository.userIdFlow } returns flowOf("user123")
        every { preferencesRepository.userPreferencesFlow } returns flowOf(
            UserPreferences(userId = "user123", userName = "Test User")
        )
        coEvery { userProfileDao.getUserProfile("user123") } returns testProfile
        val result = repository.getOrCreateCurrentUserProfile()
        assertEquals(testProfile, result)
        coVerify(exactly = 0) { userProfileDao.insert(any()) }
    }

    @Test
    fun `getOrCreateCurrentUserProfile creates new profile when not exists`() = runTest {
        every { preferencesRepository.userIdFlow } returns flowOf("newUser")
        every { preferencesRepository.userPreferencesFlow } returns flowOf(
            UserPreferences(userId = "newUser", userName = "New User")
        )
        coEvery { userProfileDao.getUserProfile("newUser") } returns null
        val profileSlot = slot<UserProfile>()
        coEvery { userProfileDao.insert(capture(profileSlot)) } returns Unit
        val result = repository.getOrCreateCurrentUserProfile()
        assertNotNull(result)
        assertEquals("newUser", result.userId)
        assertEquals("New User", result.name)
        assertEquals(0, result.reviewsCount)
        coVerify(exactly = 1) { userProfileDao.insert(any()) }
    }

    @Test
    fun `getOrCreateCurrentUserProfile uses default name when userName is empty`() = runTest {
        every { preferencesRepository.userIdFlow } returns flowOf("newUser")
        every { preferencesRepository.userPreferencesFlow } returns flowOf(
            UserPreferences(userId = "newUser", userName = "")
        )
        coEvery { userProfileDao.getUserProfile("newUser") } returns null
        val profileSlot = slot<UserProfile>()
        coEvery { userProfileDao.insert(capture(profileSlot)) } returns Unit
        val result = repository.getOrCreateCurrentUserProfile()
        assertEquals("User", result.name)
    }
}