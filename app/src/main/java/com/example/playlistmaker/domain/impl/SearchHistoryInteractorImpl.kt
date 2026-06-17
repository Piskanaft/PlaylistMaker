package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun addTrack(track: Track) {
        val history = repository.getHistory().toMutableList()
        history.removeIf { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(MAX_HISTORY_SIZE)
        }
        repository.saveHistory(history)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }

    private companion object {
        const val MAX_HISTORY_SIZE = 10
    }
}