package com.aurora.app.ui.screens.wallpaperPreview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.domain.repo.MediaRepository
import com.aurora.app.utils.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class WallpaperPreviewViewModel @Inject constructor(
    private val repository: MediaRepository
) : ViewModel() {


    private val _state: MutableStateFlow<WallpaperUiState> = MutableStateFlow(WallpaperUiState(isLoading = true))
    val state: StateFlow<WallpaperUiState> = _state.asStateFlow()

    fun loadWallpaper(wallpaperId: String, extension: String, wallpaperUrl: String) {
        _state.update { it.copy(isLoading = true)  }

        viewModelScope.launch {
            Timber.tag("loadWallpaper").e("loadWallpaper: wallpaperId: $wallpaperId, wallpaperUrl: $wallpaperUrl")
            when (val result = repository.downloadVideoIfNotExists(wallpaperId, extension, wallpaperUrl)) {
                is ResponseState.Loading -> { _state.update { it.copy(isLoading = true) } }
                is ResponseState.Error -> {
                    _state.update { it.copy(isLoading = false, isError = result.errorType.errorMessage) }
                }
                is ResponseState.Success -> {
                    _state.update { it.copy(isLoading = false, file = result.data, extension = extension) }
                }
            }
        }
    }
}

data class WallpaperUiState(
    val isLoading: Boolean = false,
    val extension: String= "",
    val file: File? = null,
    val isError: String = "",
)
