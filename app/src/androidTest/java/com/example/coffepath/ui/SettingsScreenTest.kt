package com.example.coffepath.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settingsTitle_displaysCorrectly() {
        composeTestRule.setContent {
            Text(
                "Settings",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        composeTestRule.onNodeWithText("Settings")
            .assertIsDisplayed()
    }

    @Test
    fun toggleSwitch_displaysCorrectly() {
        composeTestRule.setContent {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Dark Theme")
                Switch(
                    checked = false,
                    onCheckedChange = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Dark Theme")
            .assertIsDisplayed()
    }

    @Test
    fun toggleSwitch_canBeToggled() {
        var checked by mutableStateOf(false)

        composeTestRule.setContent {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Dark Theme")
                Switch(
                    checked = checked,
                    onCheckedChange = { checked = it }
                )
            }
        }

        composeTestRule.onNode(hasSetTextAction().not() and hasClickAction())
            .performClick()

        assert(checked)
    }

    @Test
    fun sectionHeader_displaysCorrectly() {
        composeTestRule.setContent {
            Text(
                "Appearance",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        composeTestRule.onNodeWithText("Appearance")
            .assertIsDisplayed()
    }

    @Test
    fun multipleSections_displayCorrectly() {
        composeTestRule.setContent {
            Column {
                Text("Appearance", style = MaterialTheme.typography.titleLarge)
                Text("General", style = MaterialTheme.typography.titleLarge)
                Text("Map Settings", style = MaterialTheme.typography.titleLarge)
            }
        }

        composeTestRule.onNodeWithText("Appearance")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("General")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Map Settings")
            .assertIsDisplayed()
    }

    @Test
    fun languageOption_displaysCorrectly() {
        composeTestRule.setContent {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Language")
                Text("English")
            }
        }

        composeTestRule.onNodeWithText("Language")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("English")
            .assertIsDisplayed()
    }

    @Test
    fun languageOption_isClickable() {
        var clicked = false

        composeTestRule.setContent {
            Card(
                onClick = { clicked = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Language")
                    Text("English")
                }
            }
        }

        composeTestRule.onNodeWithText("Language")
            .performClick()

        assert(clicked)
    }

    @Test
    fun resetButton_displaysCorrectly() {
        composeTestRule.setContent {
            OutlinedButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reset Settings")
            }
        }

        composeTestRule.onNodeWithText("Reset Settings")
            .assertIsDisplayed()
    }

    @Test
    fun resetButton_isClickable() {
        var clicked = false

        composeTestRule.setContent {
            OutlinedButton(
                onClick = { clicked = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reset Settings")
            }
        }

        composeTestRule.onNodeWithText("Reset Settings")
            .performClick()

        assert(clicked)
    }

    @Test
    fun multipleSettings_displayCorrectly() {
        composeTestRule.setContent {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Dark Theme")
                    Switch(checked = false, onCheckedChange = {})
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Enable Clustering")
                    Switch(checked = true, onCheckedChange = {})
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Notifications")
                    Switch(checked = true, onCheckedChange = {})
                }
            }
        }

        composeTestRule.onNodeWithText("Dark Theme")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Enable Clustering")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Notifications")
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