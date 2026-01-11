package com.example.coffepath.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.coffepath.model.Cafe
import com.example.coffepath.model.Review
import com.example.coffepath.repository.ICafeRepository
import com.example.coffepath.repository.IReviewRepository
import com.example.coffepath.repository.IUserProfileRepository
import com.example.coffepath.repository.PreferencesRepository
import com.example.coffepath.ui.screens.detail.CafeDetailUIState
import com.example.coffepath.ui.screens.detail.CafeDetailViewModel
import com.example.coffepath.viewmodel.BaseViewModelTest
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CafeDetailViewModelTest : BaseViewModelTest() {

    private lateinit var viewModel: CafeDetailViewModel
    private lateinit var cafeRepository: ICafeRepository
    private lateinit var reviewRepository: IReviewRepository
    private lateinit var userProfileRepository: IUserProfileRepository
    private lateinit var preferencesRepository: PreferencesRepository
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    override fun setup() {
        super.setup()
        cafeRepository = mockk(relaxed = true)
        reviewRepository = mockk(relaxed = true)
        userProfileRepository = mockk(relaxed = true)
        preferencesRepository = mockk(relaxed = true)

        savedStateHandle = SavedStateHandle(mapOf("cafeId" to 1L))
    }

    @Test
    fun `loadCafeDetails should load cafe and reviews successfully`() = runTest {
        val testCafe = Cafe(
            id = 1L,
            name = "Test Cafe",
            latitude = 50.0,
            longitude = 14.0,
            address = "Test Address",
            isFavorite = false
        )
        val testReviews = listOf(
            Review(
                id = 1L,
                cafeId = 1L,
                userId = "user1",
                rating = 4.5f,
                comment = "Great coffee",
                timestamp = System.currentTimeMillis()
            )
        )

        every { cafeRepository.observeCafeById(1L) } returns flowOf(testCafe)
        every { reviewRepository.getReviewsForCafe(1L) } returns flowOf(testReviews)
        coEvery { reviewRepository.getAverageRatingForCafe(1L) } returns 4.5f
        coEvery { reviewRepository.getReviewCountForCafe(1L) } returns 1

        viewModel = CafeDetailViewModel(
            savedStateHandle,
            cafeRepository,
            reviewRepository,
            userProfileRepository,
            preferencesRepository
        )

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is CafeDetailUIState.Success)
            val successState = state as CafeDetailUIState.Success
            assertEquals("Test Cafe", successState.cafe.name)
            assertEquals(1, successState.reviews.size)
            assertNotNull(successState.averageRating)
            assertEquals(4.5f, successState.averageRating!!, 0.01f)
            assertEquals(1, successState.reviewCount)
        }
    }

    @Test
    fun `toggleFavorite should update favorite status and increment count`() = runTest {
        val testCafe = Cafe(
            id = 1L,
            name = "Test Cafe",
            latitude = 50.0,
            longitude = 14.0,
            isFavorite = false
        )

        every { cafeRepository.observeCafeById(1L) } returns flowOf(testCafe)
        every { reviewRepository.getReviewsForCafe(1L) } returns flowOf(emptyList())
        coEvery { reviewRepository.getAverageRatingForCafe(1L) } returns 0f
        coEvery { reviewRepository.getReviewCountForCafe(1L) } returns 0
        every { preferencesRepository.userIdFlow } returns flowOf("user123")
        coEvery { cafeRepository.updateFavoriteStatus(any(), any()) } just Runs
        coEvery { userProfileRepository.incrementFavoritesCount(any()) } just Runs

        viewModel = CafeDetailViewModel(
            savedStateHandle,
            cafeRepository,
            reviewRepository,
            userProfileRepository,
            preferencesRepository
        )

        viewModel.toggleFavorite(false)

        coVerify { cafeRepository.updateFavoriteStatus(1L, true) }
        coVerify { userProfileRepository.incrementFavoritesCount("user123") }
    }

    @Test
    fun `toggleFavorite should decrement count when removing favorite`() = runTest {
        val testCafe = Cafe(
            id = 1L,
            name = "Test Cafe",
            latitude = 50.0,
            longitude = 14.0,
            isFavorite = true
        )

        every { cafeRepository.observeCafeById(1L) } returns flowOf(testCafe)
        every { reviewRepository.getReviewsForCafe(1L) } returns flowOf(emptyList())
        coEvery { reviewRepository.getAverageRatingForCafe(1L) } returns 0f
        coEvery { reviewRepository.getReviewCountForCafe(1L) } returns 0
        every { preferencesRepository.userIdFlow } returns flowOf("user123")
        coEvery { cafeRepository.updateFavoriteStatus(any(), any()) } just Runs
        coEvery { userProfileRepository.decrementFavoritesCount(any()) } just Runs

        viewModel = CafeDetailViewModel(
            savedStateHandle,
            cafeRepository,
            reviewRepository,
            userProfileRepository,
            preferencesRepository
        )

        viewModel.toggleFavorite(true)

        coVerify { cafeRepository.updateFavoriteStatus(1L, false) }
        coVerify { userProfileRepository.decrementFavoritesCount("user123") }
    }

    @Test
    fun `loading state should be initial state`() = runTest {
        every { cafeRepository.observeCafeById(1L) } returns flowOf(null)
        every { reviewRepository.getReviewsForCafe(1L) } returns flowOf(emptyList())

        viewModel = CafeDetailViewModel(
            savedStateHandle,
            cafeRepository,
            reviewRepository,
            userProfileRepository,
            preferencesRepository
        )

        val state = viewModel.uiState.value
        assertTrue(state is CafeDetailUIState.Loading)
    }
}