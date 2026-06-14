package com.example.playlistmaker.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Track(
    val trackName: String?,
    val artistName: String?,
    @SerializedName("trackTimeMillis") val trackTime: Long?,
    val artworkUrl100: String?,
    val trackId: Long?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
) : Serializable {

    fun getCoverArtwork() = artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")

}