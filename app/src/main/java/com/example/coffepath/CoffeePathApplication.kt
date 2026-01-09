package com.example.coffepath

import android.app.Application
import com.example.coffepath.utils.LocaleManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CoffeePathApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LocaleManager.applyLocaleToApplication(this)
    }
}