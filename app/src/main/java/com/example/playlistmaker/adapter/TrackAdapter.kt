package com.example.playlistmaker.adapter

import android.util.Log
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
    private val tracks: List<Track>
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {

        return TrackViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
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


    fun bind(model: Track) {
        trackName.text = model.trackName?.trim() ?: "Unknown"
        artistName.text = model.artistName?.trim() ?: "Unknown"
        model.artistName?.trim()?.let { Log.d("debug", it) }
        if (model.trackTime != null) {
            trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTime)
        } else {
            trackTime.text = "00:00"
        }

        Glide.with(itemView).load(model.artworkUrl100)
            .placeholder(R.drawable.album_placeholder).error(R.drawable.album_placeholder)
            .centerCrop().transform(
                RoundedCorners(
                    itemView.resources
                        .getDimensionPixelSize(R.dimen.corner_radius_minimum)
                )
            ).into(artworkUrl100)
    }
}