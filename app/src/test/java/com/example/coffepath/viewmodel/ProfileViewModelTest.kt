package com.example.coffepath.viewmodel

import android.net.Uri
import app.cash.turbine.test
import com.example.coffepath.model.Review
import com.example.coffepath.model.UserProfile
import com.example.coffepath.repository.IReviewRepository
import com.example.coffepath.repository.IUserProfileRepository
import com.example.coffepath.repository.PreferencesRepository
import com.example.coffepath.repository.UserProfileRepositoryImpl
import com.example.coffepath.viewmodel.BaseViewModelTest
import com.example.coffepath.ui.screens.profile.ProfileViewModel
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest : BaseViewModelTest() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var userProfileRepository: IUserProfileRepository
    private lateinit var reviewRepository: IReviewRepository
    private lateinit var preferencesRepository: PreferencesRepository

    @Before
    override fun setup() {
        super.setup()
        userProfileRepository = mockk<UserProfileRepositoryImpl>(relaxed = true)
        reviewRepository = mockk(relaxed = true)
        preferencesRepository = mockk(relaxed = true)

        every { preferencesRepository.userIdFlow } returns flowOf("user123")
    }

    @Test
    fun `loadProfile should load user profile and reviews successfully`() = runTest {
        val testProfile = UserProfile(
            userId = "user123",
            name = "Test User",
            photoUri = null,
            reviewsCount = 5,
            favoritesCount = 3,
            visitedCafesCount = 10
        )
        val testReviews = listOf(
            Review(
                id = 1L,
                cafeId = 1L,
                userId = "user123",
                rating = 4.5f,
                comment = "Great coffee",
                timestamp = System.currentTimeMillis()
            )
        )

        coEvery { (userProfileRepository as UserProfileRepositoryImpl).getOrCreateCurrentUserProfile() } returns testProfile
        every { userProfileRepository.getUserProfile("user123") } returns flowOf(testProfile)
        every { reviewRepository.getReviewsByUser("user123") } returns flowOf(testReviews)

        viewModel = ProfileViewModel(
            userProfileRepository,
            reviewRepository,
            preferencesRepository
        )

        viewModel.uiState.test {
            val state = awaitItem()
            assertNotNull(state.profile)
            assertEquals("Test User", state.profile?.name)
            assertEquals(1, state.recentReviews.size)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `updateProfileName should update name in repository`() = runTest {
        val testProfile = UserProfile(
            userId = "user123",
            name = "Test User",
            reviewsCount = 0,
            favoritesCount = 0,
            visitedCafesCount = 0
        )

        coEvery { (userProfileRepository as UserProfileRepositoryImpl).getOrCreateCurrentUserProfile() } returns testProfile
        every { userProfileRepository.getUserProfile("user123") } returns flowOf(testProfile)
        every { reviewRepository.getReviewsByUser("user123") } returns flowOf(emptyList())
        coEvery { userProfileRepository.updateProfileName(any(), any()) } just Runs

        viewModel = ProfileViewModel(
            userProfileRepository,
            reviewRepository,
            preferencesRepository
        )

        viewModel.updateProfileName("New Name")

        coVerify { userProfileRepository.updateProfileName("user123", "New Name") }
    }

    @Test
    fun `updateProfilePhoto should update photo in repository`() = runTest {
        val testProfile = UserProfile(
            userId = "user123",
            name = "Test User",
            reviewsCount = 0,
            favoritesCount = 0,
            visitedCafesCount = 0
        )
        val testUri = mockk<Uri>()
        every { testUri.toString() } returns "content://test/photo"

        coEvery { (userProfileRepository as UserProfileRepositoryImpl).getOrCreateCurrentUserProfile() } returns testProfile
        every { userProfileRepository.getUserProfile("user123") } returns flowOf(testProfile)
        every { reviewRepository.getReviewsByUser("user123") } returns flowOf(emptyList())
        coEvery { userProfileRepository.updateProfilePhoto(any(), any()) } just Runs

        viewModel = ProfileViewModel(
            userProfileRepository,
            reviewRepository,
            preferencesRepository
        )

        viewModel.updateProfilePhoto(testUri)

        coVerify { userProfileRepository.updateProfilePhoto("user123", "content://test/photo") }
    }

    @Test
    fun `removeProfilePhoto should clear photo`() = runTest {
        val testProfile = UserProfile(
            userId = "user123",
            name = "Test User",
            photoUri = "content://test/photo",
            reviewsCount = 0,
            favoritesCount = 0,
            visitedCafesCount = 0
        )

        coEvery { (userProfileRepository as UserProfileRepositoryImpl).getOrCreateCurrentUserProfile() } returns testProfile
        every { userProfileRepository.getUserProfile("user123") } returns flowOf(testProfile)
        every { reviewRepository.getReviewsByUser("user123") } returns flowOf(emptyList())
        coEvery { userProfileRepository.updateProfilePhoto(any(), any()) } just Runs

        viewModel = ProfileViewModel(
            userProfileRepository,
            reviewRepository,
            preferencesRepository
        )

        viewModel.removeProfilePhoto()

        coVerify { userProfileRepository.updateProfilePhoto("user123", null) }
    }

    @Test
    fun `getStatistics should return correct counts`() = runTest {
        val testProfile = UserProfile(
            userId = "user123",
            name = "Test User",
            reviewsCount = 10,
            favoritesCount = 5,
            visitedCafesCount = 15
        )

        coEvery { (userProfileRepository as UserProfileRepositoryImpl).getOrCreateCurrentUserProfile() } returns testProfile
        every { userProfileRepository.getUserProfile("user123") } returns flowOf(testProfile)
        every { reviewRepository.getReviewsByUser("user123") } returns flowOf(emptyList())

        viewModel = ProfileViewModel(
            userProfileRepository,
            reviewRepository,
            preferencesRepository
        )

        val (reviews, favorites, visited) = viewModel.getStatistics()

        assertEquals(10, reviews)
        assertEquals(5, favorites)
        assertEquals(15, visited)
    }

    @Test
    fun `toggleNameEditing should toggle editing state`() = runTest {
        val testProfile = UserProfile(
            userId = "user123",
            name = "Test User",
            reviewsCount = 0,
            favoritesCount = 0,
            visitedCafesCount = 0
        )

        coEvery { (userProfileRepository as UserProfileRepositoryImpl).getOrCreateCurrentUserProfile() } returns testProfile
        every { userProfileRepository.getUserProfile("user123") } returns flowOf(testProfile)
        every { reviewRepository.getReviewsByUser("user123") } returns flowOf(emptyList())

        viewModel = ProfileViewModel(
            userProfileRepository,
            reviewRepository,
            preferencesRepository
        )

        viewModel.toggleNameEditing()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isEditingName)
        }
    }

    @Test
    fun `togglePhotoEditing should toggle photo editing state`() = runTest {
        val testProfile = UserProfile(
            userId = "user123",
            name = "Test User",
            reviewsCount = 0,
            favoritesCount = 0,
            visitedCafesCount = 0
        )

        coEvery { (userProfileRepository as UserProfileRepositoryImpl).getOrCreateCurrentUserProfile() } returns testProfile
        every { userProfileRepository.getUserProfile("user123") } returns flowOf(testProfile)
        every { reviewRepository.getReviewsByUser("user123") } returns flowOf(emptyList())

        viewModel = ProfileViewModel(
            userProfileRepository,
            reviewRepository,
            preferencesRepository
        )

        viewModel.togglePhotoEditing()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isEditingPhoto)
        }
    }
}