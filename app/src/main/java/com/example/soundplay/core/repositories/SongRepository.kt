package com.example.soundplay.core.repositories

import com.example.soundplay.core.ResponseService
import com.example.soundplay.core.model.Song
import com.example.soundplay.core.network.ApiClient
import com.example.soundplay.core.network.SongService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Response

class SongRepository: SongService {
    private val api = ApiClient.SongApi

    override suspend fun getTracks(limit: Int): ResponseService<List<Song>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getTracks(
                    clientId = ApiClient.CLIENT_ID,
                    limit = limit
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        ResponseService.Success(body.results)
                    } else {
                        ResponseService.Error("Respuesta vacía del servidor")
                    }
                } else {
                    ResponseService.Error("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                ResponseService.Error(
                    "No se pudieron cargar las canciones: ${e.localizedMessage}"
                )
            }
        }
}