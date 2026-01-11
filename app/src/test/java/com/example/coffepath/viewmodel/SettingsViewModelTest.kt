package com.example.coffepath.viewmodel

import android.content.Context
import app.cash.turbine.test
import com.example.coffepath.R
import com.example.coffepath.repository.PreferencesRepository
import com.example.coffepath.repository.UserPreferences
import com.example.coffepath.viewmodel.BaseViewModelTest
import com.example.coffepath.ui.screens.settings.SettingsViewModel
import com.example.coffepath.utils.LocaleManager
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest : BaseViewModelTest() {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var preferencesRepository: PreferencesRepository
    private lateinit var context: Context

    @Before
    override fun setup() {
        super.setup()
        preferencesRepository = mockk(relaxed = true)
        context = mockk(relaxed = true)

        every { preferencesRepository.userPreferencesFlow } returns flowOf(UserPreferences())
        every { context.getString(R.string.english) } returns "English"
        every { context.getString(R.string.czech) } returns "Čeština"
        every { context.getString(R.string.reset_settings) } returns "Settings reset"

        mockkObject(LocaleManager)
        every { LocaleManager.saveLanguage(any(), any()) } just Runs
    }

    @Test
    fun `toggleTheme should switch theme preference`() = runTest {
        every { preferencesRepository.userPreferencesFlow } returns flowOf(
            UserPreferences(isDarkTheme = false)
        )
        coEvery { preferencesRepository.updateTheme(any()) } just Runs

        viewModel = SettingsViewModel(preferencesRepository, context)

        viewModel.toggleTheme()

        coVerify { preferencesRepository.updateTheme(true) }
    }

    @Test
    fun `changeLanguage should update language preference`() = runTest {
        coEvery { preferencesRepository.updateLanguage(any()) } just Runs

        viewModel = SettingsViewModel(preferencesRepository, context)

        viewModel.changeLanguage("cs")

        coVerify { preferencesRepository.updateLanguage("cs") }
        verify { LocaleManager.saveLanguage(context, "cs") }
    }

    @Test
    fun `changeLanguage should emit snackbar message on success`() = runTest {
        coEvery { preferencesRepository.updateLanguage(any()) } just Runs

        viewModel = SettingsViewModel(preferencesRepository, context)

        val job = launch {
            viewModel.snackbarMessage.test {
                val message = awaitItem()
                assertEquals("Language changed", message)
            }
        }

        testScheduler.advanceUntilIdle()

        viewModel.changeLanguage("en")

        testScheduler.advanceUntilIdle()
        job.cancel()
    }

    @Test
    fun `updateNotifications should update notification preference`() = runTest {
        coEvery { preferencesRepository.updateNotificationsEnabled(any()) } just Runs

        viewModel = SettingsViewModel(preferencesRepository, context)

        viewModel.updateNotifications(true)

        coVerify { preferencesRepository.updateNotificationsEnabled(true) }
    }

    @Test
    fun `resetSettings should clear preferences`() = runTest {
        coEvery { preferencesRepository.clearPreferences() } just Runs

        viewModel = SettingsViewModel(preferencesRepository, context)

        viewModel.resetSettings()

        coVerify { preferencesRepository.clearPreferences() }
    }

    @Test
    fun `resetSettings should emit snackbar message on success`() = runTest {
        coEvery { preferencesRepository.clearPreferences() } just Runs

        viewModel = SettingsViewModel(preferencesRepository, context)

        val job = launch {
            viewModel.snackbarMessage.test {
                val message = awaitItem()
                assertEquals("Settings reset", message)
            }
        }

        testScheduler.advanceUntilIdle()

        viewModel.resetSettings()

        testScheduler.advanceUntilIdle()
        job.cancel()
    }

    @Test
    fun `getAvailableLanguages should return language list`() = runTest {
        viewModel = SettingsViewModel(preferencesRepository, context)

        val languages = viewModel.getAvailableLanguages()

        assertEquals(2, languages.size)
        assertEquals("en" to "English", languages[0])
        assertEquals("cs" to "Čeština", languages[1])
    }

    @Test
    fun `getLanguageDisplayName should return correct names`() = runTest {
        viewModel = SettingsViewModel(preferencesRepository, context)

        val englishName = viewModel.getLanguageDisplayName("en")
        val czechName = viewModel.getLanguageDisplayName("cs")

        assertEquals("English", englishName)
        assertEquals("Čeština", czechName)
    }

    @Test
    fun `getLanguageDisplayName should return English for unknown code`() = runTest {
        viewModel = SettingsViewModel(preferencesRepository, context)

        val unknownName = viewModel.getLanguageDisplayName("unknown")

        assertEquals("English", unknownName)
    }

    @Test
    fun `userPreferences should emit current preferences`() = runTest {
        val testPreferences = UserPreferences(
            isDarkTheme = true,
            languageCode = "cs",
            notificationsEnabled = true
        )
        every { preferencesRepository.userPreferencesFlow } returns flowOf(testPreferences)

        viewModel = SettingsViewModel(preferencesRepository, context)

        viewModel.userPreferences.test {
            val prefs = awaitItem()
            assertTrue(prefs.isDarkTheme)
            assertEquals("cs", prefs.languageCode)
            assertTrue(prefs.notificationsEnabled)
        }
    }
}