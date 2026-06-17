package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.presentation.Creator

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val settingsInteractor = Creator.provideSettingsInteractor(this)
        val darkThemeEnabled = settingsInteractor.getThemeSettings().darkTheme
        switchTheme(darkThemeEnabled)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}