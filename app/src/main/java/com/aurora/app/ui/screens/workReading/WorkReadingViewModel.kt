package com.aurora.app.ui.screens.workReading

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.data.model.WorkDto
import com.aurora.app.domain.model.ReaderStyle
import com.aurora.app.domain.model.dashboard.WorkType
import com.aurora.app.domain.repo.MainRepository
import com.aurora.app.utils.Decrypt
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WorkReadingViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _uiState = mutableStateOf(WorkReadingUIState())
    val uiState: State<WorkReadingUIState> = _uiState

    fun initialSetup(workDto: WorkDto) {
        _uiState.value = WorkReadingUIState(workDto = workDto)
        setupUI()
        setupWorkDetails(workDto)
    }

    private fun setupUI() = viewModelScope.launch(Dispatchers.IO) {
        val style = repository.fetchReaderStyle()
        _uiState.value = _uiState.value.copy(readerStyle = style)
    }

    val gson = Gson()

    private fun setupWorkDetails(workDto: WorkDto) = viewModelScope.launch(Dispatchers.IO) {
        Timber.e("WorkReadingViewModel: setupWorkDetails called with workDto: $workDto")
        try {
            if (workDto.mType == WorkType.BOOK.type) {

                Timber.e("WorkReadingViewModel: Work type is BOOK, fetching details for id: ${workDto.id}")

                val response = repository.getPosts(id = workDto.id)
                val post = response.firstOrNull()

                if (post != null) {
                    val volumes = post.extra.parseJsonToMap().map {
                        Volume(id = it.key, title = it.value)
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        volumes = volumes,
                        postModel = response.first(),
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Post not found for id: ${workDto.id}"
                    )
                }

            } else {
                Timber.e("WorkReadingViewModel: ELSE, fetching details for id: ${workDto.id}")
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Details not found - Id: ${workDto.id}",
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            Timber.e(e)
            _uiState.value = _uiState.value.copy(
                errorMessage = "Failed to load work details",
                isLoading = false
            )
        }
    }


    fun onVolumeSelected(selectedVolume: Volume) = viewModelScope.launch(Dispatchers.IO) {
        _uiState.value = _uiState.value.copy(
            isReadingMode = true,
            selectedVolume = selectedVolume,
        )
        val volumeId = selectedVolume.id

        val volume = repository.getPosts(id = volumeId).firstOrNull()
        if (volume != null) {
            val chapters =
                volume.extra.parseJsonToMap().map { Chapter(id = it.key, title = it.value) }
            _uiState.value = _uiState.value.copy(chapters = chapters)
            onNextChapterClick()
        } else {
            Timber.e("Volume not found for id: $volumeId")
            _uiState.value = _uiState.value.copy(
                errorMessage = "Volume not found for id: $volumeId"
            )
        }
        Timber.e("onVolumeSelected: $volume")
    }


    private fun String.parseJsonToMap(): Map<String, String> {
        val type = object : TypeToken<Map<String, String>>() {}.type
        return Gson().fromJson(this, type)
    }

    fun onPreviousChapterClick() = viewModelScope.launch(Dispatchers.IO) {
        // Get the next chapter ID
        val chapters = _uiState.value.chapters // 77
        val currentIndex = _uiState.value.chapterIndex // 5
        if (currentIndex > 0) {
            val newIndex = _uiState.value.chapterIndex - 1
            _uiState.value = _uiState.value.copy(chapterIndex = newIndex)

            val previousChapter = chapters[newIndex]
            val chapterModel = repository.getPosts(previousChapter.id).firstOrNull()
            val chapter = chapterModel?.extra?.parseJsonToMap()?.toList()?.firstOrNull()
            _uiState.value = _uiState.value.copy(
                selectedChapter = previousChapter,
                chapterContent = Decrypt.decryptBookText(chapter?.second ?: ""),
            )
        } else {
            // No next chapter available
            Timber.e("No previous chapter available for index: $currentIndex")
        }
    }

    fun onNextChapterClick() = viewModelScope.launch(Dispatchers.IO) {
        val chapters = _uiState.value.chapters
        val currentIndex = _uiState.value.chapterIndex
        // Get the next chapter ID

        if (currentIndex < chapters.size - 1) {
            val newIndex = _uiState.value.chapterIndex + 1

            _uiState.value = _uiState.value.copy(chapterIndex = newIndex)
            val nextChapter = chapters[newIndex]
            val chapterModel = repository.getPosts(nextChapter.id).firstOrNull()
            val chapter = chapterModel?.extra?.parseJsonToMap()?.toList()?.firstOrNull()
            _uiState.value = _uiState.value.copy(
                selectedChapter = nextChapter,
                chapterContent = Decrypt.decryptBookText(chapter?.second ?: ""),
            )
        } else {
            // No next chapter available
            Timber.e("No next chapter available for index: $currentIndex")
        }
    }

    fun onReaderStyleChange(readerStyle: ReaderStyle) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(readerStyle = readerStyle)
            repository.setReaderStyle(readerStyle)
        }
    }

}