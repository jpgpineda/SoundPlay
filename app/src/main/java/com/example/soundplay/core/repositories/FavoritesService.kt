package com.example.soundplay.core.repositories

import com.example.soundplay.core.ResponseService
import com.example.soundplay.core.model.Song

interface FavoritesService {
    suspend fun addFavorite(song: Song): ResponseService<Unit>
    suspend fun removeFavorite(songId: String): ResponseService<Unit>
    suspend fun getFavorites(): ResponseService<List<Song>>
    suspend fun isFavorite(songId: String): ResponseService<Boolean>
}
