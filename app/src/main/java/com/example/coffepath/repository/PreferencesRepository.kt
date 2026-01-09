package com.example.coffepath.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class PreferencesRepository(private val context: Context) {
    private val dataStore = context.dataStore

    // ИСПРАВЛЕНИЕ: SharedPreferences для синхронного чтения языка
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences(
        "app_settings",
        Context.MODE_PRIVATE
    )

    private object PreferencesKeys {
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val LANGUAGE_CODE = stringPreferencesKey("language_code")
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val LAST_MAP_LATITUDE = doublePreferencesKey("last_map_latitude")
        val LAST_MAP_LONGITUDE = doublePreferencesKey("last_map_longitude")
        val LAST_MAP_ZOOM = floatPreferencesKey("last_map_zoom")
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val LAST_SYNC_TIMESTAMP = longPreferencesKey("last_sync_timestamp")
        val SEARCH_HISTORY = stringPreferencesKey("search_history")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val searchHistoryJson = preferences[PreferencesKeys.SEARCH_HISTORY] ?: "[]"
            val searchHistory = try {
                Json.decodeFromString<List<String>>(searchHistoryJson)
            } catch (e: Exception) {
                emptyList()
            }
            val languageCode = preferences[PreferencesKeys.LANGUAGE_CODE]
                ?: sharedPrefs.getString("language_code", "en")
                ?: "en"

            UserPreferences(
                userId = preferences[PreferencesKeys.USER_ID] ?: "",
                userName = preferences[PreferencesKeys.USER_NAME] ?: "User",
                languageCode = languageCode,
                isDarkTheme = preferences[PreferencesKeys.IS_DARK_THEME] ?: false,
                lastMapLatitude = preferences[PreferencesKeys.LAST_MAP_LATITUDE] ?: 49.1951,
                lastMapLongitude = preferences[PreferencesKeys.LAST_MAP_LONGITUDE] ?: 16.6068,
                lastMapZoom = preferences[PreferencesKeys.LAST_MAP_ZOOM] ?: 12f,
                isFirstLaunch = preferences[PreferencesKeys.IS_FIRST_LAUNCH] ?: true,
                notificationsEnabled = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true,
                lastSyncTimestamp = preferences[PreferencesKeys.LAST_SYNC_TIMESTAMP] ?: 0L,
                searchHistory = searchHistory
            )
        }

    suspend fun saveUserPreferences(userPreferences: UserPreferences) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = userPreferences.userId
            preferences[PreferencesKeys.USER_NAME] = userPreferences.userName
            preferences[PreferencesKeys.LANGUAGE_CODE] = userPreferences.languageCode
            preferences[PreferencesKeys.IS_DARK_THEME] = userPreferences.isDarkTheme
            preferences[PreferencesKeys.LAST_MAP_LATITUDE] = userPreferences.lastMapLatitude
            preferences[PreferencesKeys.LAST_MAP_LONGITUDE] = userPreferences.lastMapLongitude
            preferences[PreferencesKeys.LAST_MAP_ZOOM] = userPreferences.lastMapZoom
            preferences[PreferencesKeys.IS_FIRST_LAUNCH] = userPreferences.isFirstLaunch
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = userPreferences.notificationsEnabled
            preferences[PreferencesKeys.LAST_SYNC_TIMESTAMP] = userPreferences.lastSyncTimestamp
        }
        sharedPrefs.edit().putString("language_code", userPreferences.languageCode).apply()
    }

    suspend fun updateUserId(userId: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = userId
        }
    }

    suspend fun updateUserName(userName: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = userName
        }
    }

    suspend fun updateLanguage(languageCode: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE_CODE] = languageCode
        }
        sharedPrefs.edit().putString("language_code", languageCode).apply()
    }

    suspend fun toggleTheme(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_THEME] = isDark
        }
    }

    suspend fun updateMapPosition(latitude: Double, longitude: Double, zoom: Float) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_MAP_LATITUDE] = latitude
            preferences[PreferencesKeys.LAST_MAP_LONGITUDE] = longitude
            preferences[PreferencesKeys.LAST_MAP_ZOOM] = zoom
        }
    }

    suspend fun markAsLaunched() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_FIRST_LAUNCH] = false
        }
    }

    suspend fun toggleNotifications(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun updateSyncTimestamp() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_SYNC_TIMESTAMP] = System.currentTimeMillis()
        }
    }

    suspend fun clearPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
        sharedPrefs.edit().clear().apply()
    }

    suspend fun updateTheme(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_THEME] = isDark
        }
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun updateSearchHistory(history: List<String>) {
        context.dataStore.edit { preferences ->
            val json = Json.encodeToString(history)
            preferences[PreferencesKeys.SEARCH_HISTORY] = json
        }
    }

    suspend fun getUserId(): String {
        val preferences = dataStore.data.map { it[PreferencesKeys.USER_ID] }
        return preferences.catch { emit(null) }.map { it ?: "" }
            .let { flow ->
                var result = ""
                flow.collect { result = it }
                result
            }
    }

    val userIdFlow: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val existingId = preferences[PreferencesKeys.USER_ID]
            if (existingId != null) {
                existingId
            } else {
                val newId = UserPreferences.generateDefaultUserId()
                dataStore.edit { it[PreferencesKeys.USER_ID] = newId }
                newId
            }
        }
    fun getCurrentLanguage(): String {
        return sharedPrefs.getString("language_code", "en") ?: "en"
    }
}