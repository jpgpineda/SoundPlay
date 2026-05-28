package com.example.soundplay.home.songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundplay.core.ResponseService
import com.example.soundplay.core.model.Song
import com.example.soundplay.core.network.SongService
import com.example.soundplay.core.repositories.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SongsViewModel(
    private val service: SongService = SongRepository()
): ViewModel() {

    private val _songState = MutableStateFlow<ResponseService<List<Song>>?>(null)
    val songState: StateFlow<ResponseService<List<Song>>?> = _songState.asStateFlow()

    fun loadTracks(limit: Int = 20) {
        viewModelScope.launch {
            _songState.value = ResponseService.Loading
            _songState.value = service.getTracks(limit)
        }
    }
}