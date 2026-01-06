package com.example.coffepath.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class PreferencesRepository(private val context: Context) {
    private val dataStore = context.dataStore

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
            UserPreferences(
                userId = preferences[PreferencesKeys.USER_ID] ?: UserPreferences.generateDefaultUserId(),
                userName = preferences[PreferencesKeys.USER_NAME] ?: "User",
                languageCode = preferences[PreferencesKeys.LANGUAGE_CODE] ?: "en",
                isDarkTheme = preferences[PreferencesKeys.IS_DARK_THEME] ?: false,
                lastMapLatitude = preferences[PreferencesKeys.LAST_MAP_LATITUDE] ?: 49.1951,
                lastMapLongitude = preferences[PreferencesKeys.LAST_MAP_LONGITUDE] ?: 16.6068,
                lastMapZoom = preferences[PreferencesKeys.LAST_MAP_ZOOM] ?: 12f,
                isFirstLaunch = preferences[PreferencesKeys.IS_FIRST_LAUNCH] ?: true,
                notificationsEnabled = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true,
                lastSyncTimestamp = preferences[PreferencesKeys.LAST_SYNC_TIMESTAMP] ?: 0L
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
    }

    suspend fun getUserId(): String {
        val preferences = dataStore.data.map { it[PreferencesKeys.USER_ID] }
        return preferences.catch { emit(null) }.map { it ?: UserPreferences.generateDefaultUserId() }
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
            preferences[PreferencesKeys.USER_ID] ?: UserPreferences.generateDefaultUserId()
        }
}