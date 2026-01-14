package com.example.coffepath.ui.screens.review

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.coffepath.mlkit.model.DrinkType
import com.example.coffepath.navigation.INavigationRouter
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddReviewScreenUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var mockNavigation: INavigationRouter
    private lateinit var mockViewModel: AddReviewViewModel
    private lateinit var uiStateFlow: MutableStateFlow<AddReviewUIState>

    @Before
    fun setup() {
        mockNavigation = mockk(relaxed = true)
        mockViewModel = mockk(relaxed = true)
        uiStateFlow = MutableStateFlow(AddReviewUIState())

        every { mockViewModel.uiState } returns uiStateFlow
        every { mockViewModel.updateRating(any()) } just Runs
        every { mockViewModel.updateComment(any()) } just Runs
        every { mockViewModel.setPhoto(any(), any()) } just Runs
        every { mockViewModel.removePhoto() } just Runs
        every { mockViewModel.setMenuPhoto(any(), any()) } just Runs
        every { mockViewModel.removeMenuPhoto() } just Runs
        every { mockViewModel.setDrinkType(any()) } just Runs
        every { mockViewModel.clearDrinkType() } just Runs
        every { mockViewModel.reclassifyDrink() } just Runs
        every { mockViewModel.rerecognizeMenu() } just Runs
        every { mockViewModel.saveReview() } just Runs
        every { mockViewModel.clearError() } just Runs
        every { mockViewModel.resetSuccess() } just Runs
    }

    @Test
    fun topAppBar_displaysTitle() {
        uiStateFlow.value = AddReviewUIState()

        composeTestRule.setContent {
            AddReviewScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Add Review")
            .assertIsDisplayed()
    }

    @Test
    fun backButton_isDisplayed() {
        uiStateFlow.value = AddReviewUIState()

        composeTestRule.setContent {
            AddReviewScreen(
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
        uiStateFlow.value = AddReviewUIState()

        composeTestRule.setContent {
            AddReviewScreen(
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
    fun initialState_displaysCafeName() {
        uiStateFlow.value = AddReviewUIState(cafeName = "Test Cafe")

        composeTestRule.setContent {
            AddReviewScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Review for Test Cafe")
            .assertIsDisplayed()
    }

    @Test
    fun initialState_withEmptyCafeName_doesNotDisplayCafeName() {
        uiStateFlow.value = AddReviewUIState(cafeName = "")

        composeTestRule.setContent {
            AddReviewScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Review for")
            .assertDoesNotExist()
    }

    @Test
    fun sections_displayAddPhotoHeader() {
        uiStateFlow.value = AddReviewUIState()

        composeTestRule.setContent {
            AddReviewScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Add photo")
            .assertIsDisplayed()
    }

    @Test
    fun submitButton_withInvalidForm_isDisabled() {
        uiStateFlow.value = AddReviewUIState(
            rating = 0f,
            comment = ""
        )

        composeTestRule.setContent {
            AddReviewScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Submit Review")
            .assertIsNotEnabled()
    }

    @Test
    fun submitButton_withValidForm_isEnabled() {
        uiStateFlow.value = AddReviewUIState(
            rating = 4f,
            comment = "Great coffee!"
        )

        composeTestRule.setContent {
            AddReviewScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Submit Review")
            .assertIsEnabled()
    }

    @Test
    fun submitButton_whileSaving_showsProgressIndicator() {
        uiStateFlow.value = AddReviewUIState(
            rating = 4f,
            comment = "Great coffee!",
            isSaving = true
        )

        composeTestRule.setContent {
            AddReviewScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertExists()
    }

    @Test
    fun classifyingState_showsProgressIndicator() {
        uiStateFlow.value = AddReviewUIState(
            photoUri = Uri.parse("test://photo"),
            isClassifying = true
        )

        composeTestRule.setContent {
            AddReviewScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertExists()
    }

    @Test
    fun detectedDrink_unknownType_doesNotDisplayCard() {
        uiStateFlow.value = AddReviewUIState(
            photoUri = Uri.parse("test://photo"),
            detectedDrinkType = DrinkType.UNKNOWN
        )

        composeTestRule.setContent {
            AddReviewScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Drink type detected")
            .assertDoesNotExist()
    }

    @Test
    fun manualDrinkSelection_withPhotoAndNoDrink_displaysButton() {
        uiStateFlow.value = AddReviewUIState(
            photoUri = Uri.parse("test://photo"),
            detectedDrinkType = null
        )

        composeTestRule.setContent {
            AddReviewScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Select drink type manually")
            .assertIsDisplayed()
    }

    @Test
    fun manualDrinkSelection_withoutPhoto_doesNotDisplayButton() {
        uiStateFlow.value = AddReviewUIState(
            photoUri = null,
            detectedDrinkType = null
        )

        composeTestRule.setContent {
            AddReviewScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Select drink type manually")
            .assertDoesNotExist()
    }

    @Test
    fun manualDrinkSelection_withDetectedDrink_doesNotDisplayButton() {
        uiStateFlow.value = AddReviewUIState(
            photoUri = Uri.parse("test://photo"),
            detectedDrinkType = DrinkType.ESPRESSO
        )

        composeTestRule.setContent {
            AddReviewScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Select drink type manually")
            .assertDoesNotExist()
    }

    @Test
    fun manualDrinkSelectionButton_onClick_opensDialog() {
        uiStateFlow.value = AddReviewUIState(
            photoUri = Uri.parse("test://photo"),
            detectedDrinkType = null
        )

        composeTestRule.setContent {
            AddReviewScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Select drink type manually")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Select drink type")
            .assertIsDisplayed()
    }

    @Test
    fun drinkTypeDialog_displaysCancelButton() {
        uiStateFlow.value = AddReviewUIState(
            photoUri = Uri.parse("test://photo"),
            detectedDrinkType = null
        )

        composeTestRule.setContent {
            AddReviewScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Select drink type manually")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Cancel")
            .assertIsDisplayed()
    }

    @Test
    fun drinkTypeDialog_cancelButton_closesDialog() {
        uiStateFlow.value = AddReviewUIState(
            photoUri = Uri.parse("test://photo"),
            detectedDrinkType = null
        )

        composeTestRule.setContent {
            AddReviewScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Select drink type manually")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Cancel")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Select drink type")
            .assertDoesNotExist()
    }

    @Test
    fun drinkTypeDialog_displaysAvailableDrinks() {
        uiStateFlow.value = AddReviewUIState(
            photoUri = Uri.parse("test://photo"),
            detectedDrinkType = null
        )

        composeTestRule.setContent {
            AddReviewScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Select drink type manually")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Cappuccino")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Espresso")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Latte")
            .assertIsDisplayed()
    }

    @Test
    fun menuRecognizing_displaysStatusCard() {
        uiStateFlow.value = AddReviewUIState(
            menuPhotoUri = Uri.parse("test://menu"),
            isRecognizingMenu = true
        )

        composeTestRule.setContent {
            AddReviewScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Recognizing menu...")
            .assertIsDisplayed()
    }

    @Test
    fun errorState_withNoError_doesNotDisplayMessage() {
        uiStateFlow.value = AddReviewUIState(
            rating = 4f,
            comment = "Test",
            error = null
        )

        composeTestRule.setContent {
            AddReviewScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Failed to save review")
            .assertDoesNotExist()
    }

    @Test
    fun formValidation_withZeroRating_isInvalid() {
        uiStateFlow.value = AddReviewUIState(
            rating = 0f,
            comment = "Great coffee!"
        )

        composeTestRule.setContent {
            AddReviewScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Submit Review")
            .assertIsNotEnabled()
    }

    @Test
    fun formValidation_withEmptyComment_isInvalid() {
        uiStateFlow.value = AddReviewUIState(
            rating = 4f,
            comment = ""
        )

        composeTestRule.setContent {
            AddReviewScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Submit Review")
            .assertIsNotEnabled()
    }

    @Test
    fun formValidation_withBlankComment_isInvalid() {
        uiStateFlow.value = AddReviewUIState(
            rating = 4f,
            comment = "   "
        )

        composeTestRule.setContent {
            AddReviewScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Submit Review")
            .assertIsNotEnabled()
    }

    @Test
    fun formValidation_withValidData_isValid() {
        uiStateFlow.value = AddReviewUIState(
            rating = 3f,
            comment = "Nice place"
        )

        composeTestRule.setContent {
            AddReviewScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Submit Review")
            .assertIsEnabled()
    }

    @Test
    fun scrollableContent_canScrollToBottom() {
        uiStateFlow.value = AddReviewUIState(
            cafeName = "Test Cafe",
            rating = 4f,
            comment = "Great coffee!",
            photoUri = Uri.parse("test://photo"),
            detectedDrinkType = DrinkType.CAPPUCCINO
        )

        composeTestRule.setContent {
            AddReviewScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Add photo")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Submit Review")
            .performScrollTo()

        composeTestRule
            .onNodeWithText("Submit Review")
            .assertIsDisplayed()
    }
}