package com.example.playlistmaker

import android.content.SharedPreferences
import com.example.playlistmaker.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val TRACK_HISTORY_KEY = "track_history_key"

class SearchHistory(val pref: SharedPreferences) {
    private val gson = Gson()
    private val _historyTracks: ArrayList<Track> = ArrayList(read(pref))
    val historyTracks: List<Track> get() =_historyTracks

    fun addTrack(newTrack: Track) {
        _historyTracks.removeIf { it.trackId == newTrack.trackId }
        _historyTracks.add(0, newTrack)

        if (_historyTracks.size > 10) {
            _historyTracks.subList(10, _historyTracks.size).clear()
        }
        write(pref, _historyTracks)
    }

    fun clearHistory() {
        _historyTracks.clear()
        write(pref, _historyTracks)
    }

    private fun read(sharedPreferences: SharedPreferences): List<Track> {
        val json = sharedPreferences.getString(TRACK_HISTORY_KEY, null) ?: return arrayListOf()

        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    private fun write(sharedPreferences: SharedPreferences, tracks: List<Track>) {
        val json = gson.toJson(tracks)
        sharedPreferences.edit()
            .putString(TRACK_HISTORY_KEY, json)
            .apply()
    }
}