package com.example.playlistmaker

import android.content.SharedPreferences
import com.example.playlistmaker.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val TRACK_HISTORY_KEY = "track_history_key"

class SearchHistory(val pref: SharedPreferences) {

    val historyTracks: ArrayList<Track> = read(pref)

    fun addTrack(newTrack: Track) {
        historyTracks.removeIf { it.trackId == newTrack.trackId }
        historyTracks.add(0, newTrack)

        if (historyTracks.size > 10) {
            historyTracks.subList(10, historyTracks.size).clear()
        }
        write(pref, historyTracks)
    }

    fun clearHistory() {
        historyTracks.clear()
        write(pref, historyTracks)
    }

    fun read(sharedPreferences: SharedPreferences): ArrayList<Track> {
        val json = sharedPreferences.getString(TRACK_HISTORY_KEY, null) ?: return arrayListOf()

        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return Gson().fromJson(json, type) ?: arrayListOf()
    }

    fun write(sharedPreferences: SharedPreferences, tracks: ArrayList<Track>) {
        val json = Gson().toJson(tracks)
        sharedPreferences.edit()
            .putString(TRACK_HISTORY_KEY, json)
            .apply()
    }
}