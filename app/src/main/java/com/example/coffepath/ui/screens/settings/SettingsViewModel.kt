package com.example.coffepath.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffepath.repository.PreferencesRepository
import com.example.coffepath.repository.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences> = preferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    fun toggleTheme() {
        viewModelScope.launch {
            val currentTheme = userPreferences.value.isDarkTheme
            preferencesRepository.updateTheme(!currentTheme)
        }
    }

    fun changeLanguage(languageCode: String) {
        viewModelScope.launch {
            preferencesRepository.updateLanguage(languageCode)
        }
    }

    fun updateNotifications(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateNotificationsEnabled(enabled)
        }
    }
    fun resetSettings() {
        viewModelScope.launch {
            preferencesRepository.clearPreferences()
        }
    }
    fun getAvailableLanguages(): List<Pair<String, String>> {
        return listOf(
            "en" to "English",
            "cs" to "Čeština"
        )
    }
}