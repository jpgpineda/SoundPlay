package com.example.soundplay.core.model

import com.google.gson.annotations.SerializedName

data class SongResponse(
    @SerializedName("results") val results: List<Song>
)

data class Song(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("artist_name") val artistName: String,
    @SerializedName("album_name") val albumName: String,
    @SerializedName("album_image") val albumImage: String,
    @SerializedName("audio") val audio: String,
    @SerializedName("image") val image: String
)