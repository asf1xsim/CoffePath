package com.example.coffepath.ui.screens.search

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.coffepath.model.Cafe
import com.example.coffepath.model.CafeFilter
import com.example.coffepath.model.SearchHistory
import com.example.coffepath.model.SearchResult
import com.example.coffepath.navigation.INavigationRouter
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchScreenUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var mockNavigation: INavigationRouter
    private lateinit var mockViewModel: SearchViewModel
    private lateinit var uiStateFlow: MutableStateFlow<SearchUIState>

    private val testCafe = Cafe(
        id = 1L,
        name = "Test Cafe",
        latitude = 49.1951,
        longitude = 16.6068,
        address = "Test Street 123"
    )

    private val testSearchResult = SearchResult(
        cafe = testCafe,
        averageRating = 4.5f,
        reviewsCount = 10,
        distance = 0.5f
    )

    private val testSearchHistory = SearchHistory(
        query = "coffee",
        timestamp = System.currentTimeMillis()
    )

    @Before
    fun setup() {
        mockNavigation = mockk(relaxed = true)
        mockViewModel = mockk(relaxed = true)
        uiStateFlow = MutableStateFlow(SearchUIState())

        every { mockViewModel.uiState } returns uiStateFlow
        every { mockViewModel.updateQuery(any()) } just Runs
        every { mockViewModel.clearQuery() } just Runs
        every { mockViewModel.toggleFilters() } just Runs
        every { mockViewModel.updateFilter(any()) } just Runs
        every { mockViewModel.clearFilters() } just Runs
        every { mockViewModel.searchFromHistory(any()) } just Runs
        every { mockViewModel.removeFromHistory(any()) } just Runs
        every { mockViewModel.clearSearchHistory() } just Runs
    }

    @Test
    fun searchField_isDisplayed() {
        uiStateFlow.value = SearchUIState()

        composeTestRule.setContent {
            SearchScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Search cafes...")
            .assertIsDisplayed()
    }

    @Test
    fun backButton_isDisplayed() {
        uiStateFlow.value = SearchUIState()

        composeTestRule.setContent {
            SearchScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Back")
            .assertIsDisplayed()
    }

    @Test
    fun backButton_onClick_navigatesBack() {
        uiStateFlow.value = SearchUIState()

        composeTestRule.setContent {
            SearchScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        verify { mockNavigation.returnBack() }
    }

    @Test
    fun clearButton_onClick_clearsQuery() {
        uiStateFlow.value = SearchUIState(query = "coffee")

        composeTestRule.setContent {
            SearchScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Clear")
            .performClick()

        verify { mockViewModel.clearQuery() }
    }

    @Test
    fun initialState_displaysSearchMessage() {
        uiStateFlow.value = SearchUIState()

        composeTestRule.setContent {
            SearchScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Search for cafes")
            .assertIsDisplayed()
    }

    @Test
    fun loadingState_displaysProgressIndicator() {
        uiStateFlow.value = SearchUIState(isLoading = true)

        composeTestRule.setContent {
            SearchScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertIsDisplayed()
    }

    @Test
    fun emptyState_displaysNoResultsMessage() {
        uiStateFlow.value = SearchUIState(
            query = "coffee",
            searchResults = emptyList(),
            isSearchActive = true,
            isLoading = false
        )

        composeTestRule.setContent {
            SearchScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("No results found")
            .assertIsDisplayed()
    }

    @Test
    fun emptyState_displaysQueryInMessage() {
        uiStateFlow.value = SearchUIState(
            query = "coffee",
            searchResults = emptyList(),
            isSearchActive = true,
            isLoading = false
        )

        composeTestRule.setContent {
            SearchScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Try searching for \"coffee\" with different filters")
            .assertIsDisplayed()
    }

    @Test
    fun resultsState_displaysCafeName() {
        uiStateFlow.value = SearchUIState(
            query = "coffee",
            searchResults = listOf(testSearchResult),
            isSearchActive = true
        )

        composeTestRule.setContent {
            SearchScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Test Cafe")
            .assertIsDisplayed()
    }

    @Test
    fun resultsState_displaysResultsCount() {
        uiStateFlow.value = SearchUIState(
            query = "coffee",
            searchResults = listOf(testSearchResult),
            isSearchActive = true
        )

        composeTestRule.setContent {
            SearchScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("1 result")
            .assertIsDisplayed()
    }

    @Test
    fun resultsState_withMultipleResults_displaysPlural() {
        val result2 = testSearchResult.copy(cafe = testCafe.copy(id = 2L, name = "Cafe 2"))
        uiStateFlow.value = SearchUIState(
            query = "coffee",
            searchResults = listOf(testSearchResult, result2),
            isSearchActive = true
        )

        composeTestRule.setContent {
            SearchScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("2 results")
            .assertIsDisplayed()
    }

    @Test
    fun resultItem_onClick_navigatesToCafeDetail() {
        uiStateFlow.value = SearchUIState(
            query = "coffee",
            searchResults = listOf(testSearchResult),
            isSearchActive = true
        )

        composeTestRule.setContent {
            SearchScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Test Cafe")
            .performClick()

        verify { mockNavigation.navigateToCafeDetail(1L) }
    }

    @Test
    fun historyState_displaysHistoryItem() {
        uiStateFlow.value = SearchUIState(
            searchHistory = listOf(testSearchHistory),
            isSearchActive = false
        )

        composeTestRule.setContent {
            SearchScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("coffee")
            .assertIsDisplayed()
    }

    @Test
    fun filterButton_isDisplayed() {
        uiStateFlow.value = SearchUIState()

        composeTestRule.setContent {
            SearchScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Filters")
            .assertIsDisplayed()
    }

    @Test
    fun filterButton_withActiveFilters_displaysCount() {
        uiStateFlow.value = SearchUIState(
            filter = CafeFilter(minRating = 4f, hasReviews = true)
        )

        composeTestRule.setContent {
            SearchScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Filters (2)")
            .assertIsDisplayed()
    }

    @Test
    fun filterButton_onClick_opensDialog() {
        uiStateFlow.value = SearchUIState()

        composeTestRule.setContent {
            SearchScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Filters")
            .performClick()

        verify { mockViewModel.toggleFilters() }
    }



    @Test
    fun filterDialog_whenShowFiltersFalse_isNotDisplayed() {
        uiStateFlow.value = SearchUIState(showFilters = false)

        composeTestRule.setContent {
            SearchScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onAllNodesWithText("Filters")
            .assertCountEquals(1)
    }

    @Test
    fun filterDialog_hasApplyButton() {
        uiStateFlow.value = SearchUIState(showFilters = true)

        composeTestRule.setContent {
            SearchScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Apply")
            .assertIsDisplayed()
    }

    @Test
    fun filterDialog_hasCancelButton() {
        uiStateFlow.value = SearchUIState(showFilters = true)

        composeTestRule.setContent {
            SearchScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Cancel")
            .assertIsDisplayed()
    }

    @Test
    fun filterDialog_hasClearButton() {
        uiStateFlow.value = SearchUIState(showFilters = true)

        composeTestRule.setContent {
            SearchScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Clear")
            .assertIsDisplayed()
    }

    @Test
    fun filterDialog_displaysMinimumRatingOption() {
        uiStateFlow.value = SearchUIState(showFilters = true)

        composeTestRule.setContent {
            SearchScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Minimum Rating: Any")
            .assertIsDisplayed()
    }

    @Test
    fun filterDialog_displaysHasReviewsOption() {
        uiStateFlow.value = SearchUIState(showFilters = true)

        composeTestRule.setContent {
            SearchScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Has Reviews")
            .assertIsDisplayed()
    }

    @Test
    fun filterDialog_displaysFavoritesOnlyOption() {
        uiStateFlow.value = SearchUIState(showFilters = true)

        composeTestRule.setContent {
            SearchScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Favorites Only")
            .assertIsDisplayed()
    }

    @Test
    fun stateTransition_fromLoadingToResults() {
        uiStateFlow.value = SearchUIState(
            query = "coffee",
            isLoading = true
        )

        composeTestRule.setContent {
            SearchScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertIsDisplayed()

        uiStateFlow.value = SearchUIState(
            query = "coffee",
            searchResults = listOf(testSearchResult),
            isSearchActive = true,
            isLoading = false
        )

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Test Cafe")
            .assertIsDisplayed()
    }

    @Test
    fun stateTransition_fromLoadingToEmpty() {
        uiStateFlow.value = SearchUIState(
            query = "coffee",
            isLoading = true
        )

        composeTestRule.setContent {
            SearchScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        uiStateFlow.value = SearchUIState(
            query = "coffee",
            searchResults = emptyList(),
            isSearchActive = true,
            isLoading = false
        )

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("No results found")
            .assertIsDisplayed()
    }
}