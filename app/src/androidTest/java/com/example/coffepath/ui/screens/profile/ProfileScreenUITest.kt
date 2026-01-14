package com.example.coffepath.ui.screens.profile

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.coffepath.model.Review
import com.example.coffepath.model.UserProfile
import com.example.coffepath.navigation.INavigationRouter
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileScreenUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var mockNavigation: INavigationRouter
    private lateinit var mockViewModel: ProfileViewModel
    private lateinit var uiStateFlow: MutableStateFlow<ProfileUIState>

    private val testProfile = UserProfile(
        userId = "user123",
        name = "Test User",
        email = "test@example.com",
        photoUri = null,
        reviewsCount = 5,
        favoritesCount = 10,
        visitedCafesCount = 8
    )

    private val testReview = Review(
        id = 1L,
        cafeId = 100L,
        userId = "user123",
        rating = 4f,
        comment = "Great coffee!",
        timestamp = System.currentTimeMillis()
    )

    @Before
    fun setup() {
        mockNavigation = mockk(relaxed = true)
        mockViewModel = mockk(relaxed = true)
        uiStateFlow = MutableStateFlow(ProfileUIState(isLoading = true))

        every { mockViewModel.uiState } returns uiStateFlow
        every { mockViewModel.updateProfileName(any()) } just Runs
        every { mockViewModel.updateProfilePhoto(any()) } just Runs
        every { mockViewModel.removeProfilePhoto() } just Runs
        every { mockViewModel.togglePhotoEditing() } just Runs
    }

    @Test
    fun topAppBar_displaysTitle() {
        uiStateFlow.value = ProfileUIState(
            profile = testProfile,
            isLoading = false
        )

        composeTestRule.setContent {
            ProfileScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Profile")
            .assertIsDisplayed()
    }

    @Test
    fun backButton_isDisplayed() {
        uiStateFlow.value = ProfileUIState(
            profile = testProfile,
            isLoading = false
        )

        composeTestRule.setContent {
            ProfileScreen(
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
        uiStateFlow.value = ProfileUIState(
            profile = testProfile,
            isLoading = false
        )

        composeTestRule.setContent {
            ProfileScreen(
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
    fun settingsButton_isDisplayed() {
        uiStateFlow.value = ProfileUIState(
            profile = testProfile,
            isLoading = false
        )

        composeTestRule.setContent {
            ProfileScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Settings")
            .assertIsDisplayed()
    }

    @Test
    fun settingsButton_onClick_navigatesToSettings() {
        uiStateFlow.value = ProfileUIState(
            profile = testProfile,
            isLoading = false
        )

        composeTestRule.setContent {
            ProfileScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Settings")
            .performClick()

        verify { mockNavigation.navigateToSettings() }
    }

    @Test
    fun loadingState_displaysProgressIndicator() {
        uiStateFlow.value = ProfileUIState(isLoading = true)

        composeTestRule.setContent {
            ProfileScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertIsDisplayed()
    }

    @Test
    fun loadingState_doesNotDisplayContent() {
        uiStateFlow.value = ProfileUIState(isLoading = true)

        composeTestRule.setContent {
            ProfileScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Test User")
            .assertDoesNotExist()
    }

    @Test
    fun contentState_displaysProfileName() {
        uiStateFlow.value = ProfileUIState(
            profile = testProfile,
            isLoading = false
        )

        composeTestRule.setContent {
            ProfileScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Test User")
            .assertIsDisplayed()
    }

    @Test
    fun contentState_displaysProfileEmail() {
        uiStateFlow.value = ProfileUIState(
            profile = testProfile,
            isLoading = false
        )

        composeTestRule.setContent {
            ProfileScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("test@example.com")
            .assertIsDisplayed()
    }

    @Test
    fun contentState_withNullEmail_doesNotDisplayEmail() {
        val profileWithoutEmail = testProfile.copy(email = null)
        uiStateFlow.value = ProfileUIState(
            profile = profileWithoutEmail,
            isLoading = false
        )

        composeTestRule.setContent {
            ProfileScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Test User")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("test@example.com")
            .assertDoesNotExist()
    }

    @Test
    fun contentState_displaysInitialsWhenNoPhoto() {
        uiStateFlow.value = ProfileUIState(
            profile = testProfile,
            isLoading = false
        )

        composeTestRule.setContent {
            ProfileScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("TU")
            .assertIsDisplayed()
    }

    @Test
    fun editNameButton_isDisplayed() {
        uiStateFlow.value = ProfileUIState(
            profile = testProfile,
            isLoading = false
        )

        composeTestRule.setContent {
            ProfileScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Edit Name")
            .assertIsDisplayed()
    }

    @Test
    fun editNameButton_onClick_showsDialog() {
        uiStateFlow.value = ProfileUIState(
            profile = testProfile,
            isLoading = false
        )

        composeTestRule.setContent {
            ProfileScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Edit Name")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Edit Name")
            .assertIsDisplayed()
    }


    @Test
    fun nameDialog_hasSaveButton() {
        uiStateFlow.value = ProfileUIState(
            profile = testProfile,
            isLoading = false
        )

        composeTestRule.setContent {
            ProfileScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Edit Name")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Save")
            .assertIsDisplayed()
    }

    @Test
    fun nameDialog_hasCancelButton() {
        uiStateFlow.value = ProfileUIState(
            profile = testProfile,
            isLoading = false
        )

        composeTestRule.setContent {
            ProfileScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Edit Name")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Cancel")
            .assertIsDisplayed()
    }

    @Test
    fun nameDialog_cancelButton_dismissesDialog() {
        uiStateFlow.value = ProfileUIState(
            profile = testProfile,
            isLoading = false
        )

        composeTestRule.setContent {
            ProfileScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Edit Name")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Cancel")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Edit Name")
            .assertDoesNotExist()
    }


    @Test
    fun photoEditingCard_whenEnabled_isDisplayed() {
        uiStateFlow.value = ProfileUIState(
            profile = testProfile,
            isLoading = false,
            isEditingPhoto = true
        )

        composeTestRule.setContent {
            ProfileScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Update Profile Photo")
            .assertIsDisplayed()
    }

    @Test
    fun photoEditingCard_whenDisabled_isNotDisplayed() {
        uiStateFlow.value = ProfileUIState(
            profile = testProfile,
            isLoading = false,
            isEditingPhoto = false
        )

        composeTestRule.setContent {
            ProfileScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Update Profile Photo")
            .assertDoesNotExist()
    }

    @Test
    fun photoEditingCard_hasDoneButton() {
        uiStateFlow.value = ProfileUIState(
            profile = testProfile,
            isLoading = false,
            isEditingPhoto = true
        )

        composeTestRule.setContent {
            ProfileScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Done")
            .assertIsDisplayed()
    }

    @Test
    fun photoEditingCard_doneButton_togglesPhotoEditing() {
        uiStateFlow.value = ProfileUIState(
            profile = testProfile,
            isLoading = false,
            isEditingPhoto = true
        )

        composeTestRule.setContent {
            ProfileScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Done")
            .performClick()

        verify { mockViewModel.togglePhotoEditing() }
    }

    @Test
    fun statisticsSection_displaysReviewsCount() {
        uiStateFlow.value = ProfileUIState(
            profile = testProfile,
            isLoading = false
        )

        composeTestRule.setContent {
            ProfileScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("5")
            .assertIsDisplayed()
    }

    @Test
    fun statisticsSection_displaysFavoritesCount() {
        uiStateFlow.value = ProfileUIState(
            profile = testProfile,
            isLoading = false
        )

        composeTestRule.setContent {
            ProfileScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("10")
            .assertIsDisplayed()
    }

    @Test
    fun statisticsSection_displaysVisitedCount() {
        uiStateFlow.value = ProfileUIState(
            profile = testProfile,
            isLoading = false
        )

        composeTestRule.setContent {
            ProfileScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("8")
            .assertIsDisplayed()
    }

    @Test
    fun recentReviewsSection_displaysTitle() {
        uiStateFlow.value = ProfileUIState(
            profile = testProfile,
            isLoading = false
        )

        composeTestRule.setContent {
            ProfileScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Recent Reviews")
            .assertIsDisplayed()
    }

    @Test
    fun recentReviewsSection_whenEmpty_displaysEmptyState() {
        uiStateFlow.value = ProfileUIState(
            profile = testProfile,
            recentReviews = emptyList(),
            isLoading = false
        )

        composeTestRule.setContent {
            ProfileScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("No reviews yet")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Start reviewing cafes!")
            .assertIsDisplayed()
    }

    @Test
    fun recentReviewsSection_whenNotEmpty_displaysReviews() {
        uiStateFlow.value = ProfileUIState(
            profile = testProfile,
            recentReviews = listOf(testReview),
            isLoading = false
        )

        composeTestRule.setContent {
            ProfileScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Great coffee!")
            .assertIsDisplayed()
    }

    @Test
    fun recentReviewsSection_whenNotEmpty_doesNotDisplayEmptyState() {
        uiStateFlow.value = ProfileUIState(
            profile = testProfile,
            recentReviews = listOf(testReview),
            isLoading = false
        )

        composeTestRule.setContent {
            ProfileScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("No reviews yet")
            .assertDoesNotExist()
    }
}