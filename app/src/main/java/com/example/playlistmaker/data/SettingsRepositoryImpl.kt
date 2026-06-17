package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SettingsRepository
import com.example.playlistmaker.domain.models.ThemeSettings

const val SETTINGS_PREFERENCES = "playlistmaker_preferences"
const val THEME_SWITCH = "theme_switch"

class SettingsRepositoryImpl(private val sharedPreferences: SharedPreferences) : SettingsRepository {
    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(sharedPreferences.getBoolean(THEME_SWITCH, false))
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        sharedPreferences.edit()
            .putBoolean(THEME_SWITCH, settings.darkTheme)
            .apply()
    }
}