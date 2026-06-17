package com.example.playlistmaker.data

interface ExternalNavigator {
    fun shareLink(shareAppLink: String)
    fun openLink(termsLink: String)
    fun openEmail(supportEmailData: EmailData)
}

data class EmailData(val email: String, val subject: String, val text: String)
