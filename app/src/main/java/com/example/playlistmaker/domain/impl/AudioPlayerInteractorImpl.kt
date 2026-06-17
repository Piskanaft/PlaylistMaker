package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.domain.api.AudioPlayerRepository

class AudioPlayerInteractorImpl(private val repository: AudioPlayerRepository) :
    AudioPlayerInteractor {
    override fun prepare(previewUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        repository.prepare(previewUrl, onPrepared, onCompletion)
    }

    override fun start() {
        repository.start()
    }

    override fun pause() {
        repository.pause()
    }

    override fun release() {
        repository.release()
    }

    override fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }
}