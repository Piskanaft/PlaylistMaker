package com.example.playlistmaker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.model.Track
import java.text.SimpleDateFormat
import java.util.Locale


class TrackAdapter(
    private val tracks: List<Track>,
    private val onTrackClickListener: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {

        return TrackViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onTrackClickListener(track)
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}

class TrackViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
) {

    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
    private val artworkUrl100: ImageView = itemView.findViewById(R.id.artworkUrl100)
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }


    fun bind(model: Track) {
        trackName.text = model.trackName?.trim() ?: "Unknown"
        artistName.text = model.artistName?.trim() ?: "Unknown"
        if (model.trackTime != null) {
            trackTime.text = dateFormat.format(model.trackTime)
        } else {
            trackTime.text = "00:00"
        }

        val secureArtworkUrl = model.artworkUrl100?.replace("http://", "https://")

        Glide.with(itemView).load(secureArtworkUrl)
            .placeholder(R.drawable.album_placeholder)
            .error(R.drawable.album_placeholder)
            .centerCrop()
            .transform(
                RoundedCorners(
                    itemView.resources.getDimensionPixelSize(R.dimen.corner_radius_minimum)
                )
            ).into(artworkUrl100)
    }
}