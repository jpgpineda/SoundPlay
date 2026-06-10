package com.example.soundplay.core.repositories

import com.example.soundplay.core.ResponseService
import com.example.soundplay.core.model.Song
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Maneja la colección `favoritas` de Firestore.
 *
 * Estructura del documento (id = "${userId}_${songId}" para garantizar unicidad
 * por usuario y evitar duplicados):
 *  - userId: String
 *  - id, name, duration, artistName, albumName, albumImage, audio, image (campos de Song)
 */
class FavoritesRepository : FavoritesService {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val favoritesCollection = firestore.collection("favoritas")

    private fun currentUserId(): String? = auth.currentUser?.uid

    private fun docId(userId: String, songId: String) = "${userId}_$songId"

    override suspend fun addFavorite(song: Song): ResponseService<Unit> = withContext(Dispatchers.IO) {
        val userId = currentUserId()
            ?: return@withContext ResponseService.Error("Debes iniciar sesión para guardar favoritos")
        try {
            val data = mapOf(
                "userId" to userId,
                "id" to song.id,
                "name" to song.name,
                "duration" to song.duration,
                "artistName" to song.artistName,
                "albumName" to song.albumName,
                "albumImage" to song.albumImage,
                "audio" to song.audio,
                "image" to song.image
            )
            favoritesCollection.document(docId(userId, song.id))
                .set(data)
                .await()
            ResponseService.Success(Unit)
        } catch (e: Exception) {
            ResponseService.Error("No se pudo agregar a favoritos: ${e.localizedMessage}")
        }
    }

    override suspend fun removeFavorite(songId: String): ResponseService<Unit> = withContext(Dispatchers.IO) {
        val userId = currentUserId()
            ?: return@withContext ResponseService.Error("Debes iniciar sesión")
        try {
            favoritesCollection.document(docId(userId, songId))
                .delete()
                .await()
            ResponseService.Success(Unit)
        } catch (e: Exception) {
            ResponseService.Error("No se pudo eliminar de favoritos: ${e.localizedMessage}")
        }
    }

    override suspend fun getFavorites(): ResponseService<List<Song>> = withContext(Dispatchers.IO) {
        val userId = currentUserId()
            ?: return@withContext ResponseService.Error("Debes iniciar sesión")
        try {
            val snapshot = favoritesCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val songs = snapshot.documents.mapNotNull { doc ->
                val id = doc.getString("id") ?: return@mapNotNull null
                Song(
                    id = id,
                    name = doc.getString("name").orEmpty(),
                    duration = (doc.getLong("duration") ?: 0L).toInt(),
                    artistName = doc.getString("artistName").orEmpty(),
                    albumName = doc.getString("albumName").orEmpty(),
                    albumImage = doc.getString("albumImage").orEmpty(),
                    audio = doc.getString("audio").orEmpty(),
                    image = doc.getString("image").orEmpty()
                )
            }
            ResponseService.Success(songs)
        } catch (e: Exception) {
            ResponseService.Error("No se pudieron cargar las favoritas: ${e.localizedMessage}")
        }
    }

    override suspend fun isFavorite(songId: String): ResponseService<Boolean> = withContext(Dispatchers.IO) {
        val userId = currentUserId()
            ?: return@withContext ResponseService.Error("Debes iniciar sesión")
        try {
            val doc = favoritesCollection.document(docId(userId, songId))
                .get()
                .await()
            ResponseService.Success(doc.exists())
        } catch (e: Exception) {
            ResponseService.Error("No se pudo consultar favoritos: ${e.localizedMessage}")
        }
    }
}
