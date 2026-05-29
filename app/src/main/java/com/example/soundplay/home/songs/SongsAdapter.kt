package com.example.soundplay.home.songs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.soundplay.core.model.Song
import com.example.soundplay.databinding.ItemSongBinding

class SongsAdapter(
    private val onItemClick: (Song) -> Unit = {}
): ListAdapter<Song, SongsAdapter.SongViewHolder>(DIFF) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        p1: Int
    ): SongViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SongViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class SongViewHolder(
        private val binding: ItemSongBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song) {
            binding.tvTitle.text = song.name
            binding.tvArtist.text = song.artistName

            Glide.with(binding.ivCover)
                .load(song.albumImage)
                .centerCrop()
                .into(binding.ivCover)

            binding.root.setOnClickListener {
                onItemClick(song)
            }
        }
    }

    companion object {
        private val DIFF = object: DiffUtil.ItemCallback<Song>() {
            override fun areItemsTheSame(oldItem: Song, newItem: Song) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Song, newItem: Song) =
                oldItem == newItem
        }
    }
}