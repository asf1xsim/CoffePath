package com.example.coffepath.ui.screens.settings

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.coffepath.navigation.INavigationRouter
import com.example.coffepath.repository.UserPreferences
import io.mockk.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var mockNavigation: INavigationRouter
    private lateinit var mockViewModel: SettingsViewModel
    private lateinit var preferencesFlow: MutableStateFlow<UserPreferences>
    private lateinit var snackbarMessageFlow: MutableSharedFlow<String>

    @Before
    fun setup() {
        mockNavigation = mockk(relaxed = true)
        mockViewModel = mockk(relaxed = true)
        preferencesFlow = MutableStateFlow(UserPreferences())
        snackbarMessageFlow = MutableSharedFlow()

        every { mockViewModel.userPreferences } returns preferencesFlow
        every { mockViewModel.snackbarMessage } returns snackbarMessageFlow
        every { mockViewModel.toggleTheme() } just Runs
        every { mockViewModel.changeLanguage(any()) } just Runs
        every { mockViewModel.updateNotifications(any()) } just Runs
        every { mockViewModel.resetSettings() } just Runs
        every { mockViewModel.getAvailableLanguages() } returns listOf(
            "en" to "English",
            "cs" to "Czech"
        )
        every { mockViewModel.getLanguageDisplayName(any()) } answers {
            when (firstArg<String>()) {
                "en" -> "English"
                "cs" -> "Czech"
                else -> "English"
            }
        }
    }

    @Test
    fun topAppBar_displaysTitle() {
        preferencesFlow.value = UserPreferences()

        composeTestRule.setContent {
            SettingsScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Settings")
            .assertIsDisplayed()
    }

    @Test
    fun backButton_isDisplayed() {
        preferencesFlow.value = UserPreferences()

        composeTestRule.setContent {
            SettingsScreen(
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
        preferencesFlow.value = UserPreferences()

        composeTestRule.setContent {
            SettingsScreen(
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
    fun appearanceSection_isDisplayed() {
        preferencesFlow.value = UserPreferences()

        composeTestRule.setContent {
            SettingsScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Appearance")
            .assertIsDisplayed()
    }

    @Test
    fun languageSection_isDisplayed() {
        preferencesFlow.value = UserPreferences()

        composeTestRule.setContent {
            SettingsScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Language")
            .assertIsDisplayed()
    }

    @Test
    fun notificationsSection_isDisplayed() {
        preferencesFlow.value = UserPreferences()

        composeTestRule.setContent {
            SettingsScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Notifications")
            .assertIsDisplayed()
    }

    @Test
    fun aboutSection_isDisplayed() {
        preferencesFlow.value = UserPreferences()

        composeTestRule.setContent {
            SettingsScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("About")
            .assertIsDisplayed()
    }

    @Test
    fun darkThemeItem_isDisplayed() {
        preferencesFlow.value = UserPreferences()

        composeTestRule.setContent {
            SettingsScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Dark Theme")
            .assertIsDisplayed()
    }

    @Test
    fun languageItem_isDisplayed() {
        preferencesFlow.value = UserPreferences()

        composeTestRule.setContent {
            SettingsScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("App Language")
            .assertIsDisplayed()
    }

    @Test
    fun languageItem_displaysCurrentLanguage() {
        preferencesFlow.value = UserPreferences(languageCode = "en")

        composeTestRule.setContent {
            SettingsScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("English")
            .assertIsDisplayed()
    }

    @Test
    fun languageItem_onClick_opensDialog() {
        preferencesFlow.value = UserPreferences()

        composeTestRule.setContent {
            SettingsScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("App Language")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Select Language")
            .assertIsDisplayed()
    }

    @Test
    fun languageDialog_hasCancelButton() {
        preferencesFlow.value = UserPreferences()

        composeTestRule.setContent {
            SettingsScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("App Language")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Cancel")
            .assertIsDisplayed()
    }

    @Test
    fun languageDialog_cancelButton_closesDialog() {
        preferencesFlow.value = UserPreferences()

        composeTestRule.setContent {
            SettingsScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("App Language")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Cancel")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Select Language")
            .assertDoesNotExist()
    }

    @Test
    fun languageDialog_selectLanguage_callsChangeLanguage() {
        preferencesFlow.value = UserPreferences(languageCode = "en")

        composeTestRule.setContent {
            SettingsScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("App Language")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Czech")
            .performClick()

        verify { mockViewModel.changeLanguage("cs") }
    }

    @Test
    fun appVersionItem_isDisplayed() {
        preferencesFlow.value = UserPreferences()

        composeTestRule.setContent {
            SettingsScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("App Version")
            .assertIsDisplayed()
    }

    @Test
    fun appVersionItem_displaysVersion() {
        preferencesFlow.value = UserPreferences()

        composeTestRule.setContent {
            SettingsScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("1.0.0")
            .assertIsDisplayed()
    }

    @Test
    fun resetSettingsItem_isDisplayed() {
        preferencesFlow.value = UserPreferences()

        composeTestRule.setContent {
            SettingsScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Reset Settings")
            .assertIsDisplayed()
    }

    @Test
    fun resetSettingsItem_onClick_opensDialog() {
        preferencesFlow.value = UserPreferences()

        composeTestRule.setContent {
            SettingsScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Reset Settings")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Are you sure you want to reset all settings to default values? ")
            .assertIsDisplayed()
    }

    @Test
    fun resetDialog_hasResetButton() {
        preferencesFlow.value = UserPreferences()

        composeTestRule.setContent {
            SettingsScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Reset Settings")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Reset")
            .assertIsDisplayed()
    }

    @Test
    fun resetDialog_hasCancelButton() {
        preferencesFlow.value = UserPreferences()

        composeTestRule.setContent {
            SettingsScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Reset Settings")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onAllNodesWithText("Cancel")
            .assertCountEquals(1)
    }

    @Test
    fun resetDialog_cancelButton_closesDialog() {
        preferencesFlow.value = UserPreferences()

        composeTestRule.setContent {
            SettingsScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Reset Settings")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onAllNodesWithText("Cancel")[0]
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Are you sure you want to reset to default settings?")
            .assertDoesNotExist()
    }

    @Test
    fun resetDialog_resetButton_callsResetSettings() {
        preferencesFlow.value = UserPreferences()

        composeTestRule.setContent {
            SettingsScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Reset Settings")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Reset")
            .performClick()

        verify { mockViewModel.resetSettings() }
    }


    @Test
    fun stateTransition_language_updatesUI() {
        preferencesFlow.value = UserPreferences(languageCode = "en")

        composeTestRule.setContent {
            SettingsScreen(
                navigation = mockNavigation,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("English")
            .assertIsDisplayed()

        every { mockViewModel.getLanguageDisplayName("cs") } returns "Czech"
        preferencesFlow.value = UserPreferences(languageCode = "cs")

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Czech")
            .assertIsDisplayed()
    }
}