package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle

import android.widget.TextView

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar


class SettingsActivity : AppCompatActivity() {
    private val toolbar: MaterialToolbar by lazy { findViewById(R.id.toolbar) }
    private val shareButton: TextView by lazy { findViewById(R.id.action_share_app) }
    private val supportButton: TextView by lazy { findViewById(R.id.action_contact_support) }
    private val agreementButton: TextView by lazy { findViewById(R.id.action_user_agreement) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        setListeners()
    }

    private fun setListeners() {
        toolbar.setNavigationOnClickListener { finish() }

        shareButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_share_app_content))

            startActivity(
                Intent.createChooser(
                    intent, getString(R.string.settings_share_app_chooser_title)
                )
            )
        }

        supportButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.settings_support_email)))
            intent.putExtra(
                Intent.EXTRA_SUBJECT, getString(R.string.settings_support_email_subject)
            )
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_support_email_body))
            startActivity(intent)
        }

        agreementButton.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.settings_agreement_url)))
            startActivity(intent)
        }
    }
}