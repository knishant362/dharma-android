package com.aurora.app.ui.screens.ringtone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.data.local.database.entity.PostEntity
import com.aurora.app.domain.model.dashboard.Ringtone
import com.aurora.app.domain.model.dashboard.WorkType
import com.aurora.app.domain.repo.MainRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RingtoneViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _ringtones = MutableStateFlow<List<Ringtone>>(emptyList())
    val ringtones: StateFlow<List<Ringtone>> = _ringtones.asStateFlow()

    init {
        fetchRingtones()
    }

    val gson = Gson()

    private fun fetchRingtones() = viewModelScope.launch(Dispatchers.IO) {
        val posts = repository.getPostsByType(mType = WorkType.RINGTONE.type)
        val ringtones = posts.mapNotNull { post ->
            post.toRingtone()
        }
        Timber.d("Fetched Ringtones: ${ringtones.size}")
        _ringtones.value = ringtones
    }

    private fun PostEntity.toRingtone(): Ringtone? {
        try {
            Timber.e("Parsing Ringtone from PostEntity: $this")
            val model = gson.fromJson(this.data, Ringtone::class.java)
            Timber.d("Parsed Ringtone: $model")
            return model
        } catch (e: Exception) {
            Timber.e("Failed to parse Ringtone from PostEntity: ${e.message}")
            return null
        }
    }
}
