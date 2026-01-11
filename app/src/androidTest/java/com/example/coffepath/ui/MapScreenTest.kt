package com.example.coffepath.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.coffepath.navigation.INavigationRouter
import com.example.coffepath.ui.screens.map.MapTopAppBar
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockNavigation = mockk<INavigationRouter>(relaxed = true)

    @Test
    fun mapTopAppBar_displaysTitle() {
        composeTestRule.setContent {
            MapTopAppBar(
                navigation = mockNavigation,
                hasActiveFilters = false,
                filterCount = 0,
                onFilterClick = {}
            )
        }

        composeTestRule.onNodeWithText("CoffeePath")
            .assertIsDisplayed()
    }

    @Test
    fun mapTopAppBar_filterButton_isVisible() {
        composeTestRule.setContent {
            MapTopAppBar(
                navigation = mockNavigation,
                hasActiveFilters = false,
                filterCount = 0,
                onFilterClick = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Filters")
            .assertIsDisplayed()
    }

    @Test
    fun mapTopAppBar_filterButton_clickable() {
        var clicked = false

        composeTestRule.setContent {
            MapTopAppBar(
                navigation = mockNavigation,
                hasActiveFilters = false,
                filterCount = 0,
                onFilterClick = { clicked = true }
            )
        }

        composeTestRule.onNodeWithContentDescription("Filters")
            .performClick()

        assert(clicked)
    }

    @Test
    fun mapTopAppBar_showsActiveFiltersBadge() {
        composeTestRule.setContent {
            MapTopAppBar(
                navigation = mockNavigation,
                hasActiveFilters = true,
                filterCount = 3,
                onFilterClick = {}
            )
        }

        composeTestRule.onNodeWithText("3")
            .assertIsDisplayed()
    }

    @Test
    fun mapTopAppBar_searchButton_isVisible() {
        composeTestRule.setContent {
            MapTopAppBar(
                navigation = mockNavigation,
                hasActiveFilters = false,
                filterCount = 0,
                onFilterClick = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Search")
            .assertIsDisplayed()
    }

    @Test
    fun mapTopAppBar_searchButton_navigatesToSearch() {
        composeTestRule.setContent {
            MapTopAppBar(
                navigation = mockNavigation,
                hasActiveFilters = false,
                filterCount = 0,
                onFilterClick = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Search")
            .performClick()

        verify { mockNavigation.navigateToSearch() }
    }

    @Test
    fun mapTopAppBar_favoritesButton_isVisible() {
        composeTestRule.setContent {
            MapTopAppBar(
                navigation = mockNavigation,
                hasActiveFilters = false,
                filterCount = 0,
                onFilterClick = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Favorites")
            .assertIsDisplayed()
    }

    @Test
    fun mapTopAppBar_profileButton_isVisible() {
        composeTestRule.setContent {
            MapTopAppBar(
                navigation = mockNavigation,
                hasActiveFilters = false,
                filterCount = 0,
                onFilterClick = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Profile")
            .assertIsDisplayed()
    }
}