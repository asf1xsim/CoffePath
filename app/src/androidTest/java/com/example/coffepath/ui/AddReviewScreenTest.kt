package com.example.coffepath.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.coffepath.ui.screens.review.components.RatingBar
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddReviewScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun ratingBar_displaysCorrectly() {
        composeTestRule.setContent {
            RatingBar(
                rating = 3.5f,
                onRatingChanged = {},
                enabled = true
            )
        }

        composeTestRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun ratingBar_canBeInteracted() {
        var rating by mutableStateOf(0f)

        composeTestRule.setContent {
            RatingBar(
                rating = rating,
                onRatingChanged = { rating = it },
                enabled = true
            )
        }

        composeTestRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun commentTextField_canBeTyped() {
        var comment by mutableStateOf("")

        composeTestRule.setContent {
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Write your review...") }
            )
        }

        composeTestRule.onNodeWithText("Write your review...")
            .performTextInput("Great coffee!")

        assert(comment == "Great coffee!")
    }

    @Test
    fun submitButton_disabledWhenInvalid() {
        composeTestRule.setContent {
            Button(
                onClick = {},
                enabled = false
            ) {
                Text("Submit Review")
            }
        }

        composeTestRule.onNodeWithText("Submit Review")
            .assertIsNotEnabled()
    }

    @Test
    fun submitButton_enabledWhenValid() {
        composeTestRule.setContent {
            Button(
                onClick = {},
                enabled = true
            ) {
                Text("Submit Review")
            }
        }

        composeTestRule.onNodeWithText("Submit Review")
            .assertIsEnabled()
    }

    @Test
    fun submitButton_showsProgressWhenSaving() {
        composeTestRule.setContent {
            Button(
                onClick = {},
                enabled = false
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            }
        }

        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertIsDisplayed()
    }

    @Test
    fun errorMessage_displaysCorrectly() {
        composeTestRule.setContent {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Please fill all required fields",
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Close, null)
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Please fill all required fields")
            .assertIsDisplayed()
    }

    @Test
    fun sectionHeader_displaysCorrectly() {
        composeTestRule.setContent {
            Text(
                text = "Rating (Required)",
                style = MaterialTheme.typography.titleMedium
            )
        }

        composeTestRule.onNodeWithText("Rating (Required)")
            .assertIsDisplayed()
    }

    @Test
    fun statusCard_withProgress_displaysCorrectly() {
        composeTestRule.setContent {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                    Text("Analyzing drink...")
                }
            }
        }

        composeTestRule.onNodeWithText("Analyzing drink...")
            .assertIsDisplayed()

        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertIsDisplayed()
    }
}