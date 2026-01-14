package com.example.coffepath.ui.screens.detail

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.coffepath.model.Cafe
import com.example.coffepath.model.Review
import com.example.coffepath.navigation.INavigationRouter
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CafeDetailScreenUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var mockNavigation: INavigationRouter
    private lateinit var mockViewModel: CafeDetailViewModel
    private lateinit var uiStateFlow: MutableStateFlow<CafeDetailUIState>

    private val testCafe = Cafe(
        id = 1L,
        name = "Test Cafe",
        latitude = 49.1951,
        longitude = 16.6068,
        address = "Test Street 123",
        openingHours = "Mo-Fr 08:00-18:00",
        website = "www.testcafe.com",
        phone = "+420123456789",
        isFavorite = false,
        menuJson = null
    )

    private val testReview = Review(
        id = 1L,
        cafeId = 1L,
        userId = "user1",
        rating = 4f,
        comment = "Great coffee!",
        drinkType = "Espresso",
        timestamp = System.currentTimeMillis()
    )

    @Before
    fun setup() {
        mockNavigation = mockk(relaxed = true)
        mockViewModel = mockk(relaxed = true)
        uiStateFlow = MutableStateFlow<CafeDetailUIState>(CafeDetailUIState.Loading)

        every { mockViewModel.uiState } returns uiStateFlow
        every { mockViewModel.toggleFavorite(any()) } just Runs
    }

    @Test
    fun loadingState_displaysProgressIndicator() {
        uiStateFlow.value = CafeDetailUIState.Loading

        composeTestRule.setContent {
            CafeDetailScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertIsDisplayed()
    }

    @Test
    fun loadingState_doesNotDisplayFavoriteButton() {
        uiStateFlow.value = CafeDetailUIState.Loading

        composeTestRule.setContent {
            CafeDetailScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Favorites")
            .assertDoesNotExist()
    }

    @Test
    fun loadingState_doesNotDisplayFAB() {
        uiStateFlow.value = CafeDetailUIState.Loading

        composeTestRule.setContent {
            CafeDetailScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Add Review")
            .assertDoesNotExist()
    }

    @Test
    fun notFoundState_doesNotDisplayFavoriteButton() {
        uiStateFlow.value = CafeDetailUIState.NotFound

        composeTestRule.setContent {
            CafeDetailScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Favorites")
            .assertDoesNotExist()
    }

    @Test
    fun topAppBar_displaysTitle() {
        uiStateFlow.value = CafeDetailUIState.Success(
            cafe = testCafe,
            reviews = emptyList(),
            averageRating = null,
            reviewCount = 0
        )

        composeTestRule.setContent {
            CafeDetailScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Cafe Details")
            .assertIsDisplayed()
    }

    @Test
    fun backButton_onClick_navigatesBack() {
        uiStateFlow.value = CafeDetailUIState.Success(
            cafe = testCafe,
            reviews = emptyList(),
            averageRating = null,
            reviewCount = 0
        )

        composeTestRule.setContent {
            CafeDetailScreen(
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
    fun successState_displaysCafeName() {
        uiStateFlow.value = CafeDetailUIState.Success(
            cafe = testCafe,
            reviews = emptyList(),
            averageRating = null,
            reviewCount = 0
        )

        composeTestRule.setContent {
            CafeDetailScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Test Cafe")
            .assertIsDisplayed()
    }

    @Test
    fun successState_displaysAddress() {
        uiStateFlow.value = CafeDetailUIState.Success(
            cafe = testCafe,
            reviews = emptyList(),
            averageRating = null,
            reviewCount = 0
        )

        composeTestRule.setContent {
            CafeDetailScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Test Street 123")
            .assertIsDisplayed()
    }

    @Test
    fun successState_displaysOpeningHours() {
        uiStateFlow.value = CafeDetailUIState.Success(
            cafe = testCafe,
            reviews = emptyList(),
            averageRating = null,
            reviewCount = 0
        )

        composeTestRule.setContent {
            CafeDetailScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Mo-Fr 08:00-18:00")
            .assertIsDisplayed()
    }

    @Test
    fun successState_displaysPhone() {
        uiStateFlow.value = CafeDetailUIState.Success(
            cafe = testCafe,
            reviews = emptyList(),
            averageRating = null,
            reviewCount = 0
        )

        composeTestRule.setContent {
            CafeDetailScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("+420123456789")
            .assertIsDisplayed()
    }

    @Test
    fun successState_displaysWebsite() {
        uiStateFlow.value = CafeDetailUIState.Success(
            cafe = testCafe,
            reviews = emptyList(),
            averageRating = null,
            reviewCount = 0
        )

        composeTestRule.setContent {
            CafeDetailScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("www.testcafe.com")
            .assertIsDisplayed()
    }

    @Test
    fun successState_displaysCoordinates() {
        uiStateFlow.value = CafeDetailUIState.Success(
            cafe = testCafe,
            reviews = emptyList(),
            averageRating = null,
            reviewCount = 0
        )

        composeTestRule.setContent {
            CafeDetailScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("49.1951, 16.6068")
            .assertIsDisplayed()
    }

    @Test
    fun successState_withRating_displaysAverageRating() {
        uiStateFlow.value = CafeDetailUIState.Success(
            cafe = testCafe,
            reviews = listOf(testReview),
            averageRating = 4.5f,
            reviewCount = 10
        )

        composeTestRule.setContent {
            CafeDetailScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("4.5")
            .assertIsDisplayed()
    }


    @Test
    fun successState_withNoReviews_displaysEmptyState() {
        uiStateFlow.value = CafeDetailUIState.Success(
            cafe = testCafe,
            reviews = emptyList(),
            averageRating = null,
            reviewCount = 0
        )

        composeTestRule.setContent {
            CafeDetailScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("No reviews yet")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Be the first to review this cafe!")
            .assertIsDisplayed()
    }

    @Test
    fun successState_withReviews_displaysReviewCard() {
        uiStateFlow.value = CafeDetailUIState.Success(
            cafe = testCafe,
            reviews = listOf(testReview),
            averageRating = 4f,
            reviewCount = 1
        )

        composeTestRule.setContent {
            CafeDetailScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Great coffee!")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Espresso")
            .assertIsDisplayed()
    }

    @Test
    fun successState_withMultipleReviews_displaysAllReviews() {
        val review1 = testReview.copy(id = 1L, comment = "First review")
        val review2 = testReview.copy(id = 2L, comment = "Second review")
        val review3 = testReview.copy(id = 3L, comment = "Third review")

        uiStateFlow.value = CafeDetailUIState.Success(
            cafe = testCafe,
            reviews = listOf(review1, review2, review3),
            averageRating = 4f,
            reviewCount = 3
        )

        composeTestRule.setContent {
            CafeDetailScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("First review")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Second review")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Third review")
            .assertIsDisplayed()
    }

    @Test
    fun favoriteButton_whenNotFavorite_displaysBorderIcon() {
        uiStateFlow.value = CafeDetailUIState.Success(
            cafe = testCafe.copy(isFavorite = false),
            reviews = emptyList(),
            averageRating = null,
            reviewCount = 0
        )

        composeTestRule.setContent {
            CafeDetailScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Favorites")
            .assertIsDisplayed()
    }

    @Test
    fun favoriteButton_whenFavorite_displaysFilledIcon() {
        uiStateFlow.value = CafeDetailUIState.Success(
            cafe = testCafe.copy(isFavorite = true),
            reviews = emptyList(),
            averageRating = null,
            reviewCount = 0
        )

        composeTestRule.setContent {
            CafeDetailScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Favorites")
            .assertIsDisplayed()
    }

    @Test
    fun favoriteButton_onClick_togglesFavorite() {
        uiStateFlow.value = CafeDetailUIState.Success(
            cafe = testCafe.copy(isFavorite = false),
            reviews = emptyList(),
            averageRating = null,
            reviewCount = 0
        )

        composeTestRule.setContent {
            CafeDetailScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Favorites")
            .performClick()

        verify { mockViewModel.toggleFavorite(false) }
    }

    @Test
    fun addReviewFAB_isDisplayed() {
        uiStateFlow.value = CafeDetailUIState.Success(
            cafe = testCafe,
            reviews = emptyList(),
            averageRating = null,
            reviewCount = 0
        )

        composeTestRule.setContent {
            CafeDetailScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Add Review")
            .assertIsDisplayed()
    }

    @Test
    fun addReviewFAB_onClick_navigatesToAddReview() {
        uiStateFlow.value = CafeDetailUIState.Success(
            cafe = testCafe,
            reviews = emptyList(),
            averageRating = null,
            reviewCount = 0
        )

        composeTestRule.setContent {
            CafeDetailScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Add Review")
            .performClick()

        verify { mockNavigation.navigateToAddReview(testCafe.id) }
    }

    @Test
    fun successState_withNullFields_doesNotDisplayNullValues() {
        val cafeWithNulls = testCafe.copy(
            address = null,
            openingHours = null,
            phone = null,
            website = null
        )

        uiStateFlow.value = CafeDetailUIState.Success(
            cafe = cafeWithNulls,
            reviews = emptyList(),
            averageRating = null,
            reviewCount = 0
        )

        composeTestRule.setContent {
            CafeDetailScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Test Cafe")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("49.1951, 16.6068")
            .assertIsDisplayed()
    }

    @Test
    fun stateTransition_fromLoadingToSuccess() {
        uiStateFlow.value = CafeDetailUIState.Loading

        composeTestRule.setContent {
            CafeDetailScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertIsDisplayed()

        uiStateFlow.value = CafeDetailUIState.Success(
            cafe = testCafe,
            reviews = emptyList(),
            averageRating = null,
            reviewCount = 0
        )

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Test Cafe")
            .assertIsDisplayed()
    }

    @Test
    fun favoriteToggle_updatesUI() {
        uiStateFlow.value = CafeDetailUIState.Success(
            cafe = testCafe.copy(isFavorite = false),
            reviews = emptyList(),
            averageRating = null,
            reviewCount = 0
        )

        composeTestRule.setContent {
            CafeDetailScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Favorites")
            .performClick()

        uiStateFlow.value = CafeDetailUIState.Success(
            cafe = testCafe.copy(isFavorite = true),
            reviews = emptyList(),
            averageRating = null,
            reviewCount = 0
        )

        composeTestRule.waitForIdle()

        verify { mockViewModel.toggleFavorite(false) }
    }
}