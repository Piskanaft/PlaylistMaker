package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.model.Track
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Locale


class TrackActivity : AppCompatActivity() {
    private val toolbar: MaterialToolbar by lazy { findViewById(R.id.toolbar) }
    private val trackName: TextView by lazy { findViewById(R.id.trackName) }
    private val artistName: TextView by lazy { findViewById(R.id.artistName) }
    private val duration: TextView by lazy { findViewById(R.id.durationValue) }
    private val durationKey: TextView by lazy { findViewById(R.id.durationKey) }
    private val album: TextView by lazy { findViewById(R.id.albumValue) }
    private val albumKey: TextView by lazy { findViewById(R.id.albumKey) }


    private val year: TextView by lazy { findViewById(R.id.yearValue) }
    private val yearKey: TextView by lazy { findViewById(R.id.yearKey) }

    private val genre: TextView by lazy { findViewById(R.id.genreValue) }
    private val country: TextView by lazy { findViewById(R.id.countryValue) }
    private val cover: ImageView by lazy { findViewById(R.id.albumCover) }

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_track)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.track)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        toolbar.setNavigationOnClickListener { finish() }
        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getSerializableExtra(EXTRA_TRACK) as? Track

        }

        if (track != null) {
            setupTrackInfo(track)
        }
    }

    private fun setupTrackInfo(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        if (track.trackTime != null) {
            duration.text = dateFormat.format(track.trackTime)
            duration.visibility = View.VISIBLE
            durationKey.visibility = View.VISIBLE
        } else {
            duration.visibility = View.GONE
            durationKey.visibility = View.GONE
        }
        if (!track.collectionName.isNullOrEmpty()) {
            album.visibility = View.VISIBLE
            albumKey.visibility = View.VISIBLE

            album.text = track.collectionName
        } else {
            album.visibility = View.GONE
            albumKey.visibility = View.GONE
        }

        if (!track.releaseDate.isNullOrEmpty()) {
            year.visibility = View.VISIBLE
            yearKey.visibility = View.VISIBLE
            year.text = track.releaseDate.substringBefore("-")
        } else {
            year.visibility = View.GONE
            yearKey.visibility = View.GONE
        }

        genre.text = track.primaryGenreName
        country.text = track.country

        Glide.with(this).load(track.getCoverArtwork()).placeholder(R.drawable.album_placeholder)
            .centerCrop()
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.corner_radius_small)))
            .into(cover)
    }

    companion object {
        private const val EXTRA_TRACK = "extra_track"
        fun intentFactory(context: Context, track: Track): Intent {
            return Intent(context, TrackActivity::class.java).apply {
                putExtra(EXTRA_TRACK, track)
            }
        }
    }

}