package com.example.coffepath.viewmodel

import app.cash.turbine.test
import com.example.coffepath.model.*
import com.example.coffepath.repository.ICafeRepository
import com.example.coffepath.repository.IReviewRepository
import com.example.coffepath.repository.IUserProfileRepository
import com.example.coffepath.repository.PreferencesRepository
import com.example.coffepath.ui.screens.map.MainMapUIState
import com.example.coffepath.viewmodel.BaseViewModelTest
import com.example.coffepath.ui.screens.map.MainMapViewModel
import com.example.coffepath.utils.CafeVisitTracker
import com.example.coffepath.utils.LocationService
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModelTest : BaseViewModelTest() {

    private lateinit var viewModel: MainMapViewModel
    private lateinit var cafeRepository: ICafeRepository
    private lateinit var reviewRepository: IReviewRepository
    private lateinit var userProfileRepository: IUserProfileRepository
    private lateinit var preferencesRepository: PreferencesRepository
    private lateinit var locationService: LocationService
    private lateinit var cafeVisitTracker: CafeVisitTracker

    @Before
    override fun setup() {
        super.setup()
        cafeRepository = mockk(relaxed = true)
        reviewRepository = mockk(relaxed = true)
        userProfileRepository = mockk(relaxed = true)
        preferencesRepository = mockk(relaxed = true)
        locationService = mockk(relaxed = true)
        cafeVisitTracker = mockk(relaxed = true)

        every { cafeRepository.getAllCafes() } returns flowOf(emptyList())
        every { reviewRepository.getAllReviews() } returns flowOf(emptyList())
        every { locationService.hasLocationPermission() } returns false
    }

    @Test
    fun `loadCafesInArea should fetch cafes and insert them`() = runTest {
        val testCafe = Cafe(
            id = 1L,
            name = "Test Cafe",
            latitude = 50.0,
            longitude = 14.0,
            address = "Test Address",
            isFavorite = false
        )
        val response = OverpassResponse(
            elements = listOf(
                OverpassElement(
                    type = "node",
                    id = 1L,
                    lat = 50.0,
                    lon = 14.0,
                    tags = Tags(
                        name = "Test Cafe",
                        amenity = "cafe",
                        cuisine = null,
                        openingHours = null,
                        street = "Test",
                        houseNumber = "1",
                        city = "Prague",
                        website = null,
                        phone = null
                    )
                )
            )
        )

        coEvery { cafeRepository.fetchCafesFromApi(any(), any(), any(), any()) } returns Result.success(response)
        coEvery { cafeRepository.insertCafes(any()) } just Runs

        viewModel = MainMapViewModel(
            cafeRepository,
            reviewRepository,
            userProfileRepository,
            preferencesRepository,
            locationService,
            cafeVisitTracker
        )

        viewModel.loadCafesInArea(49.0, 13.0, 51.0, 15.0)

        coVerify { cafeRepository.fetchCafesFromApi(49.0, 13.0, 51.0, 15.0) }
        coVerify { cafeRepository.insertCafes(any()) }
    }

    @Test
    fun `toggleFavorite should update cafe status and user stats`() = runTest {
        every { preferencesRepository.userIdFlow } returns flowOf("user123")
        coEvery { cafeRepository.updateFavoriteStatus(any(), any()) } just Runs
        coEvery { userProfileRepository.incrementFavoritesCount(any()) } just Runs

        viewModel = MainMapViewModel(
            cafeRepository,
            reviewRepository,
            userProfileRepository,
            preferencesRepository,
            locationService,
            cafeVisitTracker
        )

        viewModel.toggleFavorite(1L, false)

        coVerify { cafeRepository.updateFavoriteStatus(1L, true) }
        coVerify { userProfileRepository.incrementFavoritesCount("user123") }
    }

    @Test
    fun `updateFilter should apply filters to cafes`() = runTest {
        val testCafes = listOf(
            Cafe(
                id = 1L,
                name = "Cafe 1",
                latitude = 50.0,
                longitude = 14.0,
                isFavorite = false
            ),
            Cafe(
                id = 2L,
                name = "Cafe 2",
                latitude = 50.1,
                longitude = 14.1,
                isFavorite = true
            )
        )

        every { cafeRepository.getAllCafes() } returns flowOf(testCafes)
        coEvery { reviewRepository.getAverageRatingForCafe(any()) } returns 0f
        coEvery { reviewRepository.getReviewCountForCafe(any()) } returns 0

        viewModel = MainMapViewModel(
            cafeRepository,
            reviewRepository,
            userProfileRepository,
            preferencesRepository,
            locationService,
            cafeVisitTracker
        )

        val filter = MapFilterState(showOnlyFavorites = true)
        viewModel.updateFilter(filter)

        viewModel.uiState.test {
            val state = awaitItem()
            if (state is MainMapUIState.Success) {
                assertEquals(1, state.filteredItems.size)
                assertEquals(2L, state.filteredItems.first().cafe.id)
            }
        }
    }

    @Test
    fun `clearFilters should reset filter state`() = runTest {
        every { cafeRepository.getAllCafes() } returns flowOf(emptyList())

        viewModel = MainMapViewModel(
            cafeRepository,
            reviewRepository,
            userProfileRepository,
            preferencesRepository,
            locationService,
            cafeVisitTracker
        )

        viewModel.clearFilters()

        viewModel.uiState.test {
            val state = awaitItem()
            if (state is MainMapUIState.Success) {
                assertFalse(state.filter.hasActiveFilters())
            }
        }
    }

    @Test
    fun `onLocationPermissionResult with granted should start location tracking`() = runTest {
        every { locationService.hasLocationPermission() } returns false
        every { locationService.getLocationUpdates() } returns flowOf()

        viewModel = MainMapViewModel(
            cafeRepository,
            reviewRepository,
            userProfileRepository,
            preferencesRepository,
            locationService,
            cafeVisitTracker
        )

        viewModel.onLocationPermissionResult(true)

        verify { locationService.getLocationUpdates() }
    }
}