package com.example.soundplay.home.playLists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundplay.core.ResponseService
import com.example.soundplay.core.model.Song
import com.example.soundplay.core.repositories.FavoritesRepository
import com.example.soundplay.core.repositories.FavoritesService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayListsViewModel(
    private val favoritesService: FavoritesService = FavoritesRepository()
) : ViewModel() {

    private val _favoritesState = MutableStateFlow<ResponseService<List<Song>>?>(null)
    val favoritesState: StateFlow<ResponseService<List<Song>>?> = _favoritesState.asStateFlow()

    fun loadFavorites() {
        viewModelScope.launch {
            _favoritesState.value = ResponseService.Loading
            _favoritesState.value = favoritesService.getFavorites()
        }
    }
}
