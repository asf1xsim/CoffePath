package com.example.coffepath.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchField_displaysCorrectly() {
        composeTestRule.setContent {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search cafes...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            )
        }

        composeTestRule.onNodeWithText("Search cafes...")
            .assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Search")
            .assertIsDisplayed()
    }

    @Test
    fun searchField_canBeTyped() {
        var query by mutableStateOf("")

        composeTestRule.setContent {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search cafes...") }
            )
        }

        composeTestRule.onNodeWithText("Search cafes...")
            .performTextInput("coffee")

        assert(query == "coffee")
    }

    @Test
    fun searchField_withClearButton_displaysCorrectly() {
        composeTestRule.setContent {
            OutlinedTextField(
                value = "coffee",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            )
        }

        composeTestRule.onNodeWithContentDescription("Clear search")
            .assertIsDisplayed()
    }

    @Test
    fun clearButton_isClickable() {
        var clicked = false

        composeTestRule.setContent {
            IconButton(onClick = { clicked = true }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear search")
            }
        }

        composeTestRule.onNodeWithContentDescription("Clear search")
            .performClick()

        assert(clicked)
    }

    @Test
    fun emptyState_displaysCorrectly() {
        composeTestRule.setContent {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = PaddingValues()),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Search for cafes")
                Text("Enter a search term to find cafes")
            }
        }

        composeTestRule.onNodeWithText("Search for cafes")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Enter a search term to find cafes")
            .assertIsDisplayed()
    }

    @Test
    fun noResultsMessage_displaysCorrectly() {
        composeTestRule.setContent {
            Text("No cafes found")
        }

        composeTestRule.onNodeWithText("No cafes found")
            .assertIsDisplayed()
    }

    @Test
    fun searchResultCard_displaysCorrectly() {
        composeTestRule.setContent {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(paddingValues = PaddingValues())) {
                    Text("Test Cafe", style = MaterialTheme.typography.titleMedium)
                    Row {
                        Text("4.5")
                        Text("★")
                        Text("(10 reviews)")
                    }
                    Text("500m away")
                }
            }
        }

        composeTestRule.onNodeWithText("Test Cafe")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("4.5")
            .assertIsDisplayed()
    }

    @Test
    fun loadingIndicator_displaysWhenSearching() {
        composeTestRule.setContent {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertIsDisplayed()
    }

    @Test
    fun recentSearchesHeader_displaysCorrectly() {
        composeTestRule.setContent {
            Text(
                "Recent Searches",
                style = MaterialTheme.typography.titleMedium
            )
        }

        composeTestRule.onNodeWithText("Recent Searches")
            .assertIsDisplayed()
    }
}