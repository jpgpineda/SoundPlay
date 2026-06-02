package com.example.soundplay.home.songDetail

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundplay.core.model.Song
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SondDetailViewModel: ViewModel() {
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

    override fun onCleared() {
        super.onCleared()
        progressJob?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}