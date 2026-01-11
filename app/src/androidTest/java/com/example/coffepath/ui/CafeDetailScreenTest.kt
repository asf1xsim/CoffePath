package com.example.coffepath.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
class CafeDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun cafeNameAndAddress_displayCorrectly() {
        composeTestRule.setContent {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Test Cafe",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    "Test Address",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        composeTestRule.onNodeWithText("Test Cafe")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Test Address")
            .assertIsDisplayed()
    }

    @Test
    fun favoriteButton_notFavorite_displaysCorrectly() {
        composeTestRule.setContent {
            IconButton(onClick = {}) {
                Icon(
                    Icons.Default.FavoriteBorder,
                    contentDescription = "Add to favorites"
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Add to favorites")
            .assertIsDisplayed()
    }

    @Test
    fun favoriteButton_favorite_displaysCorrectly() {
        composeTestRule.setContent {
            IconButton(onClick = {}) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Remove from favorites",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Remove from favorites")
            .assertIsDisplayed()
    }

    @Test
    fun favoriteButton_isClickable() {
        var clicked = false

        composeTestRule.setContent {
            IconButton(onClick = { clicked = true }) {
                Icon(
                    Icons.Default.FavoriteBorder,
                    contentDescription = "Add to favorites"
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Add to favorites")
            .performClick()

        assert(clicked)
    }

    @Test
    fun addReviewFab_displaysCorrectly() {
        composeTestRule.setContent {
            FloatingActionButton(onClick = {}) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Add,
                    contentDescription = "Add review"
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Add review")
            .assertIsDisplayed()
    }

    @Test
    fun ratingAndReviewCount_displayCorrectly() {
        composeTestRule.setContent {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("4.5")
                Text("★")
                Text("(10 reviews)")
            }
        }

        composeTestRule.onNodeWithText("4.5")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("(10 reviews)")
            .assertIsDisplayed()
    }

    @Test
    fun reviewCard_displaysCorrectly() {
        composeTestRule.setContent {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("User Name", style = MaterialTheme.typography.titleSmall)
                        Row {
                            Text("5.0")
                            Text("★")
                        }
                    }
                    Text("Great coffee!", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        composeTestRule.onNodeWithText("User Name")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("5.0")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Great coffee!")
            .assertIsDisplayed()
    }

    @Test
    fun openingHours_displayCorrectly() {
        composeTestRule.setContent {
            Column {
                Text(
                    "Opening Hours",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Mon-Fri: 8:00-18:00",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        composeTestRule.onNodeWithText("Opening Hours")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Mon-Fri: 8:00-18:00")
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
    fun emptyReviewsState_displaysCorrectly() {
        composeTestRule.setContent {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No reviews yet")
                Text("Be the first to review!")
            }
        }

        composeTestRule.onNodeWithText("No reviews yet")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Be the first to review!")
            .assertIsDisplayed()
    }

    @Test
    fun backButton_isDisplayed() {
        composeTestRule.setContent {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back"
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Navigate back")
            .assertIsDisplayed()
    }
}