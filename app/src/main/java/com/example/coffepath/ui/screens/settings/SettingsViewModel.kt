package com.example.coffepath.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffepath.R
import com.example.coffepath.repository.PreferencesRepository
import com.example.coffepath.repository.UserPreferences
import com.example.coffepath.utils.LocaleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences> = preferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    fun toggleTheme() {
        viewModelScope.launch {
            val currentTheme = userPreferences.value.isDarkTheme
            preferencesRepository.updateTheme(!currentTheme)
        }
    }

    fun changeLanguage(languageCode: String) {
        viewModelScope.launch {
            try {
                preferencesRepository.updateLanguage(languageCode)
                LocaleManager.saveLanguage(context, languageCode)
                _snackbarMessage.emit("Language changed")
            } catch (e: Exception) {
                _snackbarMessage.emit("Failed to change language: ${e.message}")
            }
        }
    }

    fun updateNotifications(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateNotificationsEnabled(enabled)
        }
    }

    fun resetSettings() {
        viewModelScope.launch {
            try {
                preferencesRepository.clearPreferences()
                _snackbarMessage.emit(context.getString(R.string.reset_settings))
            } catch (e: Exception) {
                _snackbarMessage.emit("Failed to reset settings: ${e.message}")
            }
        }
    }

    fun getAvailableLanguages(): List<Pair<String, String>> {
        return listOf(
            "en" to context.getString(R.string.english),
            "cs" to context.getString(R.string.czech)
        )
    }

    fun getLanguageDisplayName(languageCode: String): String {
        return when (languageCode) {
            "cs" -> context.getString(R.string.czech)
            "en" -> context.getString(R.string.english)
            else -> context.getString(R.string.english)
        }
    }
}