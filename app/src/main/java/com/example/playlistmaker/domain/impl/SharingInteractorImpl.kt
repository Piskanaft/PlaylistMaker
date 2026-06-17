package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.EmailData
import com.example.playlistmaker.data.ExternalNavigator
import com.example.playlistmaker.domain.api.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val shareAppLink: String,
    private val termsLink: String,
    private val supportEmailData: EmailData
) : SharingInteractor {
    override fun shareApp() = externalNavigator.shareLink(shareAppLink)
    override fun openTerms() = externalNavigator.openLink(termsLink)
    override fun openSupport() = externalNavigator.openEmail(supportEmailData)
}