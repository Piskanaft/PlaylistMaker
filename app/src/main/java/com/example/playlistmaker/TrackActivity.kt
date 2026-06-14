package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Locale


class TrackActivity : AppCompatActivity() {
    private var mediaPlayer = MediaPlayer()
    private val toolbar: MaterialToolbar by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.toolbar) }
    private val trackName: TextView by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.trackName) }
    private val artistName: TextView by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.artistName) }
    private val duration: TextView by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.durationValue) }
    private val durationKey: TextView by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.durationKey) }
    private val album: TextView by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.albumValue) }
    private val albumKey: TextView by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.albumKey) }
    private val year: TextView by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.yearValue) }
    private val yearKey: TextView by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.yearKey) }
    private val genre: TextView by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.genreValue) }
    private val country: TextView by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.countryValue) }
    private val cover: ImageView by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.albumCover) }
    private val playPauseButton: MaterialButton by lazy(mode = LazyThreadSafetyMode.NONE) {
        findViewById(
            R.id.playPauseButton
        )
    }
    private val currentPlaytime: TextView by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.current_playtime) }
    private var track: Track? = null
    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = Runnable { updateTimer() }
    private var playerState = STATE_DEFAULT

    private val dateFormat by lazy(mode = LazyThreadSafetyMode.NONE) {
        SimpleDateFormat(
            "mm:ss", Locale.getDefault()
        )
    }

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
        track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getSerializableExtra(EXTRA_TRACK) as? Track

        }

        track?.let {
            setupTrackInfo(it)
            preparePlayer()
        }

        playPauseButton.setOnClickListener { playbackControl() }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(timerRunnable)
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

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track?.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playPauseButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playPauseButton.setIconResource(R.drawable.play_button)
            playerState = STATE_PREPARED
            handler.removeCallbacks(timerRunnable)
            currentPlaytime.text = "00:00"
        }
    }


    private fun startPlayer() {
        mediaPlayer.start()
        playPauseButton.setIconResource(R.drawable.pause_button)
        playerState = STATE_PLAYING
        handler.post(timerRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playPauseButton.setIconResource(R.drawable.play_button)
        playerState = STATE_PAUSED
        handler.removeCallbacks(timerRunnable)
    }

    private fun updateTimer() {
        when (playerState) {
            STATE_PLAYING -> {
                currentPlaytime.text = dateFormat.format(mediaPlayer.currentPosition)
                handler.postDelayed(timerRunnable, 200L)
            }
        }
    }


    companion object {
        private const val EXTRA_TRACK = "extra_track"
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        fun intentFactory(context: Context, track: Track): Intent {
            return Intent(context, TrackActivity::class.java).apply {
                putExtra(EXTRA_TRACK, track)
            }
        }
    }

}