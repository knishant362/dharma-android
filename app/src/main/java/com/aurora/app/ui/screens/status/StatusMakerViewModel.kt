package com.aurora.app.ui.screens.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.domain.model.dashboard.StatusDto
import com.aurora.app.domain.model.dashboard.WorkType
import com.aurora.app.domain.model.dashboard.toStatusDto
import com.aurora.app.domain.repo.MainRepository
import com.aurora.app.ui.screens.status.model.SocialStats
import com.aurora.app.ui.screens.status.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatusMakerState(
    val statusVideos: List<StatusDto> = emptyList(),
    val userProfile: UserProfile? = null,
    val socialStats: SocialStats? = null
)

@HiltViewModel
class StatusMakerViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _state: MutableStateFlow<StatusMakerState> = MutableStateFlow(StatusMakerState())
    val state = _state.asStateFlow()

    init {
        fetchWallpapers()
    }

    fun fetchWallpapers() = viewModelScope.launch(Dispatchers.IO) {
        val result = repository.getPostsByType(WorkType.STATUS.type)
        val statusData = result.mapNotNull {
            if (it.content_type == "video/mp4") {
                it.data.toStatusDto(it._id)
            } else null
        }
        _state.update {
            it.copy(
                statusVideos = statusData,
                userProfile = UserProfile(
                    name = "Sanatan Dharam",
                    businessName = "Dharam Enterprises",
                    address = "123 Delhi St, Delhi",
                    profileImage = "https://example.com/profile.jpg"
                ),
                socialStats = SocialStats(
                    whatsappShares = 150,
                    likes = 300,
                    views = 5000
                )
            )
        }
    }
}