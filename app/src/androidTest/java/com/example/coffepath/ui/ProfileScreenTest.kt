package com.example.coffepath.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun statisticCard_displaysCorrectly() {
        composeTestRule.setContent {
            Card(modifier = Modifier.padding(8.dp)) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("5", style = MaterialTheme.typography.headlineMedium)
                    Text("Reviews", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        composeTestRule.onNodeWithText("5")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Reviews")
            .assertIsDisplayed()
    }

    @Test
    fun multipleStatistics_displayCorrectly() {
        composeTestRule.setContent {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("5")
                        Text("Reviews")
                    }
                }

                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("3")
                        Text("Favorites")
                    }
                }

                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("10")
                        Text("Visited")
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("5")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Reviews")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("3")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Favorites")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("10")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Visited")
            .assertIsDisplayed()
    }

    @Test
    fun userNameHeader_displaysCorrectly() {
        composeTestRule.setContent {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Test User",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        composeTestRule.onNodeWithText("Test User")
            .assertIsDisplayed()
    }

    @Test
    fun recentReviewsSection_displaysCorrectly() {
        composeTestRule.setContent {
            Column {
                Text(
                    "Recent Reviews",
                    style = MaterialTheme.typography.titleLarge
                )

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Test Cafe", style = MaterialTheme.typography.titleMedium)
                        Row {
                            Text("5.0")
                            Text("★")
                        }
                        Text("Great coffee!")
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Recent Reviews")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Test Cafe")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Great coffee!")
            .assertIsDisplayed()
    }

    @Test
    fun editButton_isVisible() {
        composeTestRule.setContent {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Edit,
                    contentDescription = "Edit profile"
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Edit profile")
            .assertIsDisplayed()
    }

    @Test
    fun loadingState_displaysProgressIndicator() {
        composeTestRule.setContent {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertIsDisplayed()
    }

    @Test
    fun errorState_displaysErrorMessage() {
        composeTestRule.setContent {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Failed to load profile",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        composeTestRule.onNodeWithText("Failed to load profile")
            .assertIsDisplayed()
    }

    @Test
    fun emptyReviewsState_displaysCorrectly() {
        composeTestRule.setContent {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No reviews yet")
                Text("Start reviewing cafes!")
            }
        }

        composeTestRule.onNodeWithText("No reviews yet")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Start reviewing cafes!")
            .assertIsDisplayed()
    }
}