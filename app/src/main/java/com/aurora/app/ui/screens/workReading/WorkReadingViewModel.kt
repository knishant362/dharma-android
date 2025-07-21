package com.aurora.app.ui.screens.workReading

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.data.model.WorkDto
import com.aurora.app.domain.model.dashboard.WorkType
import com.aurora.app.domain.repo.MainRepository
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
        setupWorkDetails(workDto)
    }

    val gson = Gson()


    private fun setupWorkDetails(workDto: WorkDto) = viewModelScope.launch(Dispatchers.IO) {
        Timber.e("WorkReadingViewModel: setupWorkDetails called with workDto: $workDto")
        try {
            if (workDto.mType == WorkType.BOOK.type) {

                Timber.e("WorkReadingViewModel: Work type is BOOK, fetching details for id: ${workDto.id}")

                val response = repository.getPosts(id = workDto.id)
                val post = response.firstOrNull()

                if (post != null){
                    val volumes = post.extra.parseJsonToMap()
//                val chapters = repository.getPosts(firstVolumeKey)
//                    .firstOrNull()?.toString()?.let { extra -> getVolumesMap(extra) } ?: emptyMap()

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

    private fun setupWorkDetails2(workDto: WorkDto) = viewModelScope.launch(Dispatchers.IO) {
        Timber.e("WorkReadingViewModel: setupWorkDetails called with workDto: $workDto")
        try {
            if (workDto.mType == WorkType.BOOK.type) {

                Timber.e("WorkReadingViewModel: Work type is BOOK, fetching details for id: ${workDto.id}")

                val response = repository.getPosts(id = workDto.id)
                Timber.e("Response from getPostsById: $response")
                val volumes = (response.first().extra).parseJsonToMap()
                val firstVolumeKey = volumes.keys.firstOrNull() ?: ""
                val firstVolumeText = volumes[firstVolumeKey] ?: ""
                val selectedVolume = Pair(firstVolumeKey, firstVolumeText)

                val chapters = repository.getPosts(firstVolumeKey).firstOrNull()?.toString()?.parseJsonToMap() ?: emptyMap()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    volumes = volumes,
                    postModel = response.first(),
                    selectedVolume = selectedVolume,
                    chapters = chapters
                )
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

    fun onVolumeSelected(selectedVolume: Pair<String, String>) = viewModelScope.launch(Dispatchers.IO) {
        _uiState.value = _uiState.value.copy(
            isReadingMode = true,
            selectedVolume = selectedVolume,
        )

        val volumeId = selectedVolume.first

        val volume = repository.getPosts(id = volumeId).firstOrNull()
        if (volume != null) {
            val chapters = volume.extra.parseJsonToMap()
            val selectedChapter = chapters.toList().toList()[0]
            _uiState.value = _uiState.value.copy(
                selectedChapter = selectedChapter,
                chapters = chapters
            )

            val chapterModel = repository.getPosts(selectedChapter.first).firstOrNull()
            Timber.e("Chapter model: $chapterModel")
            val chapter = chapterModel?.extra?.parseJsonToMap() ?: emptyMap()
            Timber.e("Chapter content: $chapter")
            _uiState.value = _uiState.value.copy(
                selectedChapter = selectedChapter,
                chapters = chapters,
                chapter = chapter.toList().firstOrNull()?.second ?: ""
            )

        } else {
            Timber.e("Volume not found for id: $volumeId")
            _uiState.value = _uiState.value.copy(
                errorMessage = "Volume not found for id: $volumeId"
            )
        }



        Timber.e("onVolumeSelected: $volume")
    }

    fun onNextChapterClick() = viewModelScope.launch {
//        val isReadingMode = _uiState.value.isReadingMode
//        if (isReadingMode) {
//            val currentVolume = _uiState.value.selectedVolume
//            val chapters = _uiState.value.chapters
//            val currentChapterId = _uiState.value.selectedChapter.first
//
//            // Get the next chapter ID
//            val chapterIds = chapters.keys.toList()
//            val currentIndex = chapterIds.indexOf(currentChapterId)
//            if (currentIndex < chapterIds.size - 1) {
//                val nextChapterId = chapterIds[currentIndex + 1]
//                _uiState.value = _uiState.value.copy(
//                    selectedChapter = Pair(nextChapterId, chapters[nextChapterId] ?: ""),
//                )
//            } else {
//                val volumeIds = _uiState.value.volumes.keys.toList()
//                val currentVolumeIndex = volumeIds.indexOf(currentVolume.first)
//                if (currentVolumeIndex < volumeIds.size - 1) {
//                    val nextVolumeId = volumeIds[currentVolumeIndex + 1]
//                    val nextVolumeName = _uiState.value.volumes[nextVolumeId] ?: ""
//                    _uiState.value = _uiState.value.copy(
//                        selectedVolume = Pair(nextVolumeId, nextVolumeName),
//                        selectedChapter = Pair("", "")
//                    )
//                } else {
//                    // No more volumes available
//                }
//            }
//        }
    }

//    fun getVolumesMap(jsonString: String): Map<String, String> {
//        return try {
//            val type = object : TypeToken<Map<String, String>>() {}.type
//            val volumeMap: Map<String, String> = gson.fromJson(jsonString, type) ?: emptyMap()
//            return volumeMap
//        } catch (e: Exception) {
//            e.printStackTrace()
//            emptyMap()
//        }
//    }

    fun String.parseJsonToMap(): Map<String, String> {
        val type = object : TypeToken<Map<String, String>>() {}.type
        return Gson().fromJson(this, type)
    }

}