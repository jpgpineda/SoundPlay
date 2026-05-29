package com.example.soundplay.core.network

import com.example.soundplay.core.model.SongResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

interface SongAPI {
    @GET("v3.0/tracks/")
    suspend fun getTracks(
        @Query("client_id") clientId: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 20
    ): Response<SongResponse>
}