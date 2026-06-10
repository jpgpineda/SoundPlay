package com.example.soundplay.home.songDetail

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundplay.core.ResponseService
import com.example.soundplay.core.model.Song
import com.example.soundplay.core.repositories.FavoritesRepository
import com.example.soundplay.core.repositories.FavoritesService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SondDetailViewModel(
    private val favoritesService: FavoritesService = FavoritesRepository()
): ViewModel() {
    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0)
    val currentPositionMs: StateFlow<Int> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0)
    val durationMs: StateFlow<Int> = _durationMs.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _favoriteError = MutableStateFlow<String?>(null)
    val favoriteError: StateFlow<String?> = _favoriteError.asStateFlow()

    fun prepare(song: Song) {
        // Si ya estaba preparado para esta canción, no hacer nada
        if (mediaPlayer != null) return

        mediaPlayer = MediaPlayer().apply {
            setDataSource(song.audio)
            setOnPreparedListener {
                _durationMs.value = duration
                _isReady.value = true
            }
            setOnCompletionListener {
                _isPlaying.value = false
                _currentPositionMs.value = 0
                seekTo(0)
            }
            prepareAsync()
        }
    }

    fun togglePlayPause() {
        val player = mediaPlayer ?: return
        if (player.isPlaying) {
            player.pause()
            _isPlaying.value = false
            progressJob?.cancel()
        } else {
            player.start()
            _isPlaying.value = true
            startProgressUpdates()
        }
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.seekTo(positionMs)
        _currentPositionMs.value = positionMs
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (mediaPlayer?.isPlaying == true) {
                _currentPositionMs.value = mediaPlayer?.currentPosition ?: 0
                delay(500)
            }
        }
    }

    fun checkFavorite(songId: String) {
        viewModelScope.launch {
            when (val result = favoritesService.isFavorite(songId)) {
                is ResponseService.Success -> _isFavorite.value = result.data
                is ResponseService.Error -> _favoriteError.value = result.error
                else -> Unit
            }
        }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            val result = if (_isFavorite.value) {
                favoritesService.removeFavorite(song.id)
            } else {
                favoritesService.addFavorite(song)
            }
            when (result) {
                is ResponseService.Success -> _isFavorite.value = !_isFavorite.value
                is ResponseService.Error -> _favoriteError.value = result.error
                else -> Unit
            }
        }
    }

    fun consumeFavoriteError() {
        _favoriteError.value = null
    }

    override fun onCleared() {
        super.onCleared()
        progressJob?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}