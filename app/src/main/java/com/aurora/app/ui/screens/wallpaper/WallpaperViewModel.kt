package com.aurora.app.ui.screens.wallpaper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.domain.model.dashboard.WorkType
import com.aurora.app.domain.model.wallpaper.WallpaperSectionDto
import com.aurora.app.domain.model.wallpaper.WallpaperSectionView
import com.aurora.app.domain.model.wallpaper.toWallpaperDto
import com.aurora.app.domain.repo.MainRepository
import com.aurora.app.utils.ResponseState
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WallpaperListViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    val gson = Gson()

    private val state = MutableStateFlow(WallpaperListUiState(isLoading = true))
    val uiState = state.asStateFlow()

    fun fetchWallpapers() = viewModelScope.launch(Dispatchers.IO) {
        val result = mainRepository.getWallpapersData("hi:page:wallapaper_page")
        Timber.tag("fetchWallpapers").d("fetchWallpapers: ${result.data?.sections}")
        when (result) {
            is ResponseState.Loading -> state.update { it.copy(isLoading = true) }
            is ResponseState.Success -> {
                val response = result.data?.sections ?: emptyList()
                val wallpaperSections = response.map { prepareWallpaperSections(it) }.filter { it.wallpapers.isNotEmpty() }
                delay(500)
                state.update {
                    it.copy(
                        wallpaperSections = wallpaperSections,
                        rawSections = response,
                        isLoading = false
                    )
                }
            }

            is ResponseState.Error -> state.update {
                it.copy(
                    errorMessages = result.errorType.errorMessage,
                    isLoading = false
                )
            }
        }
    }

    private suspend fun prepareWallpaperSections(section: WallpaperSectionDto): WallpaperSectionView {
        val type = when (section.stype) {
            "GOD_WALLPAPER" -> WorkType.WALLPAPER
//            "GODS" -> WorkType.GOD //Todo("Uncomment when ALL Screen with Gods Filters is implemented")
            "TOP_LIVE_WALLPAPER" -> WorkType.LIVE_WALLPAPER
            else -> null
        }

        if (type == null) {
            Timber.e("Unknown section type: ${section.stype}")
            return WallpaperSectionView(
                id = section.title,
                title = section.title,
                wallpapers = emptyList()
            )
        }

        val response = mainRepository.getPostsByType(type.type)
        Timber.d("prepareWallpaperSections: ${section.stype} : ${response.size}")

        val wallpapers = when (section.stype) {
            "GOD_WALLPAPER" -> response.filter { it.topics == section.data }
            else -> response
        }.mapNotNull { it.data.toWallpaperDto(it._id) }

        return WallpaperSectionView(
            id = section.title,
            title = section.title,
            wallpapers = wallpapers.shuffled()
        )
    }

    fun refreshWallpapers() {


    }

}