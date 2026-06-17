package com.example.playlistmaker.presentation.ui

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.App
import com.example.playlistmaker.presentation.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.ThemeSettings
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial


class SettingsActivity : AppCompatActivity() {
    private val toolbar: MaterialToolbar by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.toolbar) }
    private val shareButton: TextView by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.action_share_app) }
    private val supportButton: TextView by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.action_contact_support) }
    private val agreementButton: TextView by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.action_user_agreement) }
    private val themeSwitcher by lazy(mode = LazyThreadSafetyMode.NONE) {
        findViewById<SwitchMaterial>(
            R.id.themeSwitcher
        )
    }
    private val settingsInteractor by lazy { Creator.provideSettingsInteractor(this) }
    private val sharingInteractor by lazy { Creator.provideSharingInteractor(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        themeSwitcher.isChecked = settingsInteractor.getThemeSettings().darkTheme
        setListeners()
    }

    private fun setListeners() {
        toolbar.setNavigationOnClickListener { finish() }

        shareButton.setOnClickListener {
            sharingInteractor.shareApp()
        }

        supportButton.setOnClickListener {
            sharingInteractor.openSupport()
        }

        agreementButton.setOnClickListener {
            sharingInteractor.openTerms()
        }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            val newSettings = ThemeSettings(checked)
            settingsInteractor.updateThemeSetting(newSettings)
            (applicationContext as App).switchTheme(checked)

        }
    }
}