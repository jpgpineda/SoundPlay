package com.example.soundplay.home.songs

import androidx.lifecycle.ViewModel
import com.example.soundplay.core.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SongsSharedViewModel: ViewModel() {
    private val _selectedSong = MutableStateFlow<Song?>(null)
    val selectedSong: StateFlow<Song?> = _selectedSong.asStateFlow()

    fun selectSong(song: Song) {
        _selectedSong.value = song
    }
}