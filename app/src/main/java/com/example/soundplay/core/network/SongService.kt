package com.example.soundplay.core.network

import com.example.soundplay.core.ResponseService
import com.example.soundplay.core.model.Song

interface SongService {
    suspend fun getTracks(limit: Int = 20): ResponseService<List<Song>>
}