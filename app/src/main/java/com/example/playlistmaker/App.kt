package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
const val PLAYLISTMAKER_PREFERENCES = "playlistmaker_preferences"
const val THEME_SWITCH = "theme_switch"
class App : Application() {
    var darkTheme = false
        private set

    override fun onCreate() {
        super.onCreate()
        val sharedPrefs = getSharedPreferences(PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(THEME_SWITCH, false)
        switchTheme(darkTheme)
    }
    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}