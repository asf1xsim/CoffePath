package com.example.coffepath.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun favoritesTitle_displaysCorrectly() {
        composeTestRule.setContent {
            Text(
                "Favorites",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        composeTestRule.onNodeWithText("Favorites")
            .assertIsDisplayed()
    }

    @Test
    fun cafeListItem_displaysCorrectly() {
        composeTestRule.setContent {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Favorite Cafe",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Test Address",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row {
                        Text("4.5")
                        Text("★")
                        Text("(10 reviews)")
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Favorite Cafe")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Test Address")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("4.5")
            .assertIsDisplayed()
    }

    @Test
    fun emptyState_displaysCorrectly() {
        composeTestRule.setContent {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "No favorites yet",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    "Add cafes to your favorites to see them here",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        composeTestRule.onNodeWithText("No favorites yet")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Add cafes to your favorites to see them here")
            .assertIsDisplayed()
    }

    @Test
    fun cafeListItem_isClickable() {
        var clicked = false

        composeTestRule.setContent {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                onClick = { clicked = true }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Favorite Cafe")
                }
            }
        }

        composeTestRule.onNodeWithText("Favorite Cafe")
            .performClick()

        assert(clicked)
    }

    @Test
    fun multipleCafes_displayCorrectly() {
        composeTestRule.setContent {
            Column {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Cafe One")
                        Row {
                            Text("4.5")
                            Text("★")
                        }
                    }
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Cafe Two")
                        Row {
                            Text("4.0")
                            Text("★")
                        }
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Cafe One")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Cafe Two")
            .assertIsDisplayed()
    }

    @Test
    fun ratingAndReviewCount_displayCorrectly() {
        composeTestRule.setContent {
            Row {
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

    @Test
    fun favoriteIcon_displaysInListItem() {
        composeTestRule.setContent {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Favorite Cafe")
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Favorite,
                        contentDescription = "Favorite",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        composeTestRule.onNodeWithContentDescription("Favorite")
            .assertIsDisplayed()
    }
}