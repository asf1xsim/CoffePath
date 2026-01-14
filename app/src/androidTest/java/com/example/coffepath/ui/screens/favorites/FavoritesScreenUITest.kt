package com.example.coffepath.ui.screens.favorites

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.coffepath.model.Cafe
import com.example.coffepath.navigation.INavigationRouter
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritesScreenUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var mockNavigation: INavigationRouter
    private lateinit var mockViewModel: FavoritesViewModel
    private lateinit var favoriteCafesFlow: MutableStateFlow<List<Cafe>>

    private val testCafe1 = Cafe(
        id = 1L,
        name = "Test Cafe 1",
        latitude = 49.1951,
        longitude = 16.6068,
        address = "Test Street 123",
        openingHours = "Mo-Fr 08:00-18:00",
        isFavorite = true
    )

    private val testCafe2 = Cafe(
        id = 2L,
        name = "Test Cafe 2",
        latitude = 49.2,
        longitude = 16.7,
        address = "Second Street 456",
        openingHours = "Mo-Su 09:00-20:00",
        isFavorite = true
    )

    private val testCafe3 = Cafe(
        id = 3L,
        name = "Test Cafe 3",
        latitude = 49.3,
        longitude = 16.8,
        address = "Third Street 789",
        isFavorite = true
    )

    @Before
    fun setup() {
        mockNavigation = mockk(relaxed = true)
        mockViewModel = mockk(relaxed = true)
        favoriteCafesFlow = MutableStateFlow(emptyList())

        every { mockViewModel.favoriteCafes } returns favoriteCafesFlow
    }

    @Test
    fun topAppBar_displaysTitle() {
        favoriteCafesFlow.value = emptyList()

        composeTestRule.setContent {
            FavoritesScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Favorite Cafes")
            .assertIsDisplayed()
    }

    @Test
    fun backButton_isDisplayed() {
        favoriteCafesFlow.value = emptyList()

        composeTestRule.setContent {
            FavoritesScreen(
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
        favoriteCafesFlow.value = emptyList()

        composeTestRule.setContent {
            FavoritesScreen(
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
    fun emptyState_displaysNoFavoritesText() {
        favoriteCafesFlow.value = emptyList()

        composeTestRule.setContent {
            FavoritesScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("No favorite cafes yet")
            .assertIsDisplayed()
    }

    @Test
    fun emptyState_displaysHintText() {
        favoriteCafesFlow.value = emptyList()

        composeTestRule.setContent {
            FavoritesScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Add cafes to favorites from the map")
            .assertIsDisplayed()
    }

    @Test
    fun listState_displaysCafeName() {
        favoriteCafesFlow.value = listOf(testCafe1)

        composeTestRule.setContent {
            FavoritesScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Test Cafe 1")
            .assertIsDisplayed()
    }

    @Test
    fun listState_displaysCafeAddress() {
        favoriteCafesFlow.value = listOf(testCafe1)

        composeTestRule.setContent {
            FavoritesScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Test Street 123")
            .assertIsDisplayed()
    }

    @Test
    fun listState_displaysCafeOpeningHours() {
        favoriteCafesFlow.value = listOf(testCafe1)

        composeTestRule.setContent {
            FavoritesScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Hours: Mo-Fr 08:00-18:00")
            .assertIsDisplayed()
    }

    @Test
    fun listState_withNullAddress_doesNotDisplayAddress() {
        val cafeWithoutAddress = testCafe1.copy(address = null)
        favoriteCafesFlow.value = listOf(cafeWithoutAddress)

        composeTestRule.setContent {
            FavoritesScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Test Cafe 1")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Test Street 123")
            .assertDoesNotExist()
    }

    @Test
    fun listState_withNullOpeningHours_doesNotDisplayHours() {
        val cafeWithoutHours = testCafe3.copy(openingHours = null)
        favoriteCafesFlow.value = listOf(cafeWithoutHours)

        composeTestRule.setContent {
            FavoritesScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Test Cafe 3")
            .assertIsDisplayed()
    }

    @Test
    fun cafeItem_onClick_navigatesToDetail() {
        favoriteCafesFlow.value = listOf(testCafe1)

        composeTestRule.setContent {
            FavoritesScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Test Cafe 1")
            .performClick()

        verify { mockNavigation.navigateToCafeDetail(1L) }
    }

    @Test
    fun listState_withMultipleCafes_displaysAllCafes() {
        favoriteCafesFlow.value = listOf(testCafe1, testCafe2, testCafe3)

        composeTestRule.setContent {
            FavoritesScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Test Cafe 1")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Test Cafe 2")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Test Cafe 3")
            .assertIsDisplayed()
    }

    @Test
    fun listState_clickingDifferentCafes_navigatesToCorrectDetails() {
        favoriteCafesFlow.value = listOf(testCafe1, testCafe2)

        composeTestRule.setContent {
            FavoritesScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Test Cafe 1")
            .performClick()

        verify { mockNavigation.navigateToCafeDetail(1L) }

        composeTestRule
            .onNodeWithText("Test Cafe 2")
            .performClick()

        verify { mockNavigation.navigateToCafeDetail(2L) }
    }

    @Test
    fun emptyState_doesNotDisplayListItems() {
        favoriteCafesFlow.value = emptyList()

        composeTestRule.setContent {
            FavoritesScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Test Cafe 1")
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText("No favorite cafes yet")
            .assertIsDisplayed()
    }

    @Test
    fun listState_doesNotDisplayEmptyState() {
        favoriteCafesFlow.value = listOf(testCafe1)

        composeTestRule.setContent {
            FavoritesScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Test Cafe 1")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("No favorite cafes yet")
            .assertDoesNotExist()
    }

    @Test
    fun stateTransition_fromEmptyToList() {
        favoriteCafesFlow.value = emptyList()

        composeTestRule.setContent {
            FavoritesScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("No favorite cafes yet")
            .assertIsDisplayed()

        favoriteCafesFlow.value = listOf(testCafe1)

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Test Cafe 1")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("No favorite cafes yet")
            .assertDoesNotExist()
    }

    @Test
    fun stateTransition_fromListToEmpty() {
        favoriteCafesFlow.value = listOf(testCafe1)

        composeTestRule.setContent {
            FavoritesScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Test Cafe 1")
            .assertIsDisplayed()

        favoriteCafesFlow.value = emptyList()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("No favorite cafes yet")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Test Cafe 1")
            .assertDoesNotExist()
    }

    @Test
    fun cafeItem_displaysAsCard() {
        favoriteCafesFlow.value = listOf(testCafe1)

        composeTestRule.setContent {
            FavoritesScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNode(
                hasText("Test Cafe 1") and
                        hasClickAction()
            )
            .assertIsDisplayed()
    }

    @Test
    fun backButton_worksInEmptyState() {
        favoriteCafesFlow.value = emptyList()

        composeTestRule.setContent {
            FavoritesScreen(
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
    fun backButton_worksInListState() {
        favoriteCafesFlow.value = listOf(testCafe1, testCafe2)

        composeTestRule.setContent {
            FavoritesScreen(
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
    fun listState_updatingCafe_refreshesUI() {
        favoriteCafesFlow.value = listOf(testCafe1)

        composeTestRule.setContent {
            FavoritesScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Test Cafe 1")
            .assertIsDisplayed()

        val updatedCafe = testCafe1.copy(name = "Updated Cafe Name")
        favoriteCafesFlow.value = listOf(updatedCafe)

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Updated Cafe Name")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Test Cafe 1")
            .assertDoesNotExist()
    }
}