package com.example.coffepath.ui.screens.map

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.coffepath.model.*
import com.example.coffepath.navigation.INavigationRouter
import com.google.android.gms.maps.model.LatLng
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapScreenUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var mockNavigation: INavigationRouter
    private lateinit var mockViewModel: MainMapViewModel
    private lateinit var uiStateFlow: MutableStateFlow<MainMapUIState>
    private lateinit var userLocationFlow: MutableStateFlow<LatLng?>
    private lateinit var locationPermissionFlow: MutableStateFlow<Boolean>

    @Before
    fun setup() {
        mockNavigation = mockk(relaxed = true)
        mockViewModel = mockk(relaxed = true)

        uiStateFlow = MutableStateFlow<MainMapUIState>(MainMapUIState.Loading)
        userLocationFlow = MutableStateFlow<LatLng?>(null)
        locationPermissionFlow = MutableStateFlow(false)

        every { mockViewModel.uiState } returns uiStateFlow
        every { mockViewModel.userLocation } returns userLocationFlow
        every { mockViewModel.locationPermissionGranted } returns locationPermissionFlow
        every { mockViewModel.toggleFilterSheet() } just Runs
        every { mockViewModel.updateFilter(any()) } just Runs
        every { mockViewModel.clearFilters() } just Runs
    }

    @Test
    fun successState_displaysTopAppBar() {
        uiStateFlow.value = MainMapUIState.Success()

        composeTestRule.setContent {
            MapScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("CoffeePath")
            .assertIsDisplayed()
    }

    @Test
    fun topAppBar_displaysAllButtons() {
        uiStateFlow.value = MainMapUIState.Success()

        composeTestRule.setContent {
            MapScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Filters")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Search")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Favorites")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Profile")
            .assertIsDisplayed()
    }

    @Test
    fun filterButton_onClick_togglesFilterSheet() {
        uiStateFlow.value = MainMapUIState.Success()

        composeTestRule.setContent {
            MapScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Filters")
            .performClick()

        verify { mockViewModel.toggleFilterSheet() }
    }

    @Test
    fun searchButton_onClick_navigatesToSearch() {
        uiStateFlow.value = MainMapUIState.Success()

        composeTestRule.setContent {
            MapScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Search")
            .performClick()

        verify { mockNavigation.navigateToSearch() }
    }

    @Test
    fun favoritesButton_onClick_navigatesToFavorites() {
        uiStateFlow.value = MainMapUIState.Success()

        composeTestRule.setContent {
            MapScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Favorites")
            .performClick()

        verify { mockNavigation.navigateToFavorites() }
    }

    @Test
    fun profileButton_onClick_navigatesToProfile() {
        uiStateFlow.value = MainMapUIState.Success()

        composeTestRule.setContent {
            MapScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Profile")
            .performClick()

        verify { mockNavigation.navigateToProfile() }
    }

    @Test
    fun topAppBar_withActiveFilters_displaysBadge() {
        val filterState = MapFilterState(
            showOnlyFavorites = true,
            minRating = 4f
        )
        uiStateFlow.value = MainMapUIState.Success(filter = filterState)

        composeTestRule.setContent {
            MapScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("2")
            .assertIsDisplayed()
    }

    @Test
    fun mapContent_displaysLoadCafesButton() {
        uiStateFlow.value = MainMapUIState.Success()

        composeTestRule.setContent {
            MapScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Load Cafes")
            .assertIsDisplayed()
    }

    @Test
    fun mapContent_whenLoading_displaysProgressIndicator() {
        uiStateFlow.value = MainMapUIState.Success(isLoading = true)

        composeTestRule.setContent {
            MapScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag("progress_indicator", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun mapContent_withLocationPermission_displaysMyLocationButton() {
        uiStateFlow.value = MainMapUIState.Success()
        locationPermissionFlow.value = true
        userLocationFlow.value = LatLng(49.1951, 16.6068)

        composeTestRule.setContent {
            MapScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("My Location")
            .assertIsDisplayed()
    }

    @Test
    fun filterBottomSheet_clearAllButton_onClick_clearsFilters() {
        val filterState = MapFilterState(showOnlyFavorites = true)
        uiStateFlow.value = MainMapUIState.Success(
            filter = filterState,
            showFilterSheet = true
        )

        composeTestRule.setContent {
            MapScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Clear All")
            .performClick()

        verify { mockViewModel.clearFilters() }
    }

    @Test
    fun filterBottomSheet_applyButton_onClick_appliesFilters() {
        uiStateFlow.value = MainMapUIState.Success(showFilterSheet = true)

        composeTestRule.setContent {
            MapScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Apply Filters")
            .performClick()

        verify { mockViewModel.updateFilter(any()) }
        verify { mockViewModel.toggleFilterSheet() }
    }

    @Test
    fun filterBottomSheet_reviewsSwitch_canBeToggled() {
        uiStateFlow.value = MainMapUIState.Success(showFilterSheet = true)

        composeTestRule.setContent {
            MapScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        val switchNode = composeTestRule
            .onAllNodes(hasAnyAncestor(hasText("Has Reviews")) and hasClickAction())
            .onLast()

        switchNode.assertIsDisplayed()
        switchNode.performClick()
    }

    @Test
    fun filterBottomSheet_ratingSlider_displaysCorrectValue() {
        val filterState = MapFilterState(minRating = 4f)
        uiStateFlow.value = MainMapUIState.Success(
            filter = filterState,
            showFilterSheet = true
        )

        composeTestRule.setContent {
            MapScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("4")
            .assertIsDisplayed()
    }

    @Test
    fun filterBottomSheet_ratingSlider_withNoRating_displaysAny() {
        uiStateFlow.value = MainMapUIState.Success(showFilterSheet = true)

        composeTestRule.setContent {
            MapScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Any")
            .assertIsDisplayed()
    }

    @Test
    fun fullFilterWorkflow_openApplyAndClose() {
        uiStateFlow.value = MainMapUIState.Success()

        composeTestRule.setContent {
            MapScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Filters")
            .performClick()

        verify { mockViewModel.toggleFilterSheet() }

        uiStateFlow.value = MainMapUIState.Success(showFilterSheet = true)

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Filters")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Apply Filters")
            .performClick()

        verify { mockViewModel.updateFilter(any()) }
        verify(atLeast = 2) { mockViewModel.toggleFilterSheet() }
    }

    @Test
    fun stateTransition_fromLoadingToSuccess() {
        uiStateFlow.value = MainMapUIState.Loading

        composeTestRule.setContent {
            MapScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Loading map...")
            .assertIsDisplayed()

        uiStateFlow.value = MainMapUIState.Success()

        composeTestRule
            .onNodeWithText("CoffeePath")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Load Cafes")
            .assertIsDisplayed()
    }

    @Test
    fun stateTransition_fromSuccessToError() {
        uiStateFlow.value = MainMapUIState.Success()

        composeTestRule.setContent {
            MapScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        val errorMessage = "Network error"
        uiStateFlow.value = MainMapUIState.Error(errorMessage)

        composeTestRule
            .onNodeWithText("Error loading map")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(errorMessage)
            .assertIsDisplayed()
    }
}