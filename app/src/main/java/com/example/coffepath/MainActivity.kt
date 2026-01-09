package com.example.coffepath

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.coffepath.navigation.NavGraph
import com.example.coffepath.repository.PreferencesRepository
import com.example.coffepath.ui.theme.CoffeePathTheme
import com.example.coffepath.utils.LocaleManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var preferencesRepository: PreferencesRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyLocale()

        enableEdgeToEdge()
        setContent {
            val preferences by preferencesRepository.userPreferencesFlow.collectAsState(
                initial = com.example.coffepath.repository.UserPreferences()
            )

            CoffeePathTheme(darkTheme = preferences.isDarkTheme) {
                NavGraph()
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val languageCode = LocaleManager.getLanguage(newBase)
        val context = LocaleManager.setLocale(newBase, languageCode)
        super.attachBaseContext(context)
    }

    private fun applyLocale() {
        val languageCode = LocaleManager.getLanguage(this)
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(resources.configuration)
        config.setLocale(locale)

        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}