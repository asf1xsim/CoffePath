package com.example.coffepath.repository

import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val userId: String = "",
    val userName: String = "User",
    val languageCode: String = "en",
    val isDarkTheme: Boolean = false,
    val lastMapLatitude: Double = 49.1951,
    val lastMapLongitude: Double = 16.6068,
    val lastMapZoom: Float = 12f,
    val isFirstLaunch: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val lastSyncTimestamp: Long = 0L,
    val searchHistory: List<String> = emptyList()
) {
    companion object {
        fun generateDefaultUserId(): String {
            return "user_${System.currentTimeMillis()}_${(1000..9999).random()}"
        }

        fun default(): UserPreferences {
            return UserPreferences(userId = generateDefaultUserId())
        }
    }

    fun isCzechLanguage(): Boolean = languageCode == "cs"

    fun isEnglishLanguage(): Boolean = languageCode == "en"

    fun updateMapPosition(latitude: Double, longitude: Double, zoom: Float): UserPreferences {
        return this.copy(
            lastMapLatitude = latitude,
            lastMapLongitude = longitude,
            lastMapZoom = zoom
        )
    }

    fun toggleTheme(): UserPreferences {
        return this.copy(isDarkTheme = !isDarkTheme)
    }

    fun changeLanguage(newLanguageCode: String): UserPreferences {
        return this.copy(languageCode = newLanguageCode)
    }

    fun markAsLaunched(): UserPreferences {
        return this.copy(isFirstLaunch = false)
    }

    fun updateSyncTimestamp(): UserPreferences {
        return this.copy(lastSyncTimestamp = System.currentTimeMillis())
    }
}