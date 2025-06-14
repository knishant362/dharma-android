package com.aurora.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.domain.repo.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUIState())
    val uiState: StateFlow<ProfileUIState> = _uiState

    private val _events = MutableSharedFlow<ProfileEvent>(replay = 0)
    val events = _events.asSharedFlow()


    init {
        viewModelScope.launch {
            val profile = repository.getUserProfile()
            _uiState.update {
                it.copy(
                    name = profile.name,
                    gender = profile.gender,
                    dateOfBirth = profile.dateOfBirth,
                    relationshipStatus = profile.relationshipStatus,
                    occupation = profile.occupation
                )
            }
        }
    }

    fun onFieldChange(updated: ProfileUIState) {
        _uiState.value = updated
    }

    fun saveProfile() = viewModelScope.launch {
        with(uiState.value) {
            val errors = mutableMapOf<String, String?>()

            if (name.isBlank()) errors["name"] = "Name cannot be empty"
            if (gender.isBlank()) errors["gender"] = "Gender cannot be empty"
            if (dateOfBirth.isBlank()) errors["dob"] = "Date of Birth cannot be empty"
//            if (relationshipStatus.isBlank()) errors["relationship"] =
//                "Relationship status required"
//            if (occupation.isBlank()) errors["occupation"] = "Occupation cannot be empty"

            _uiState.value = uiState.value.copy(errors = errors)

            if (errors.isEmpty()) {
                repository.saveUserProfile(
                    name = name,
                    gender = gender,
                    dateOfBirth = dateOfBirth,
                    relationshipStatus = relationshipStatus,
                    occupation = occupation
                )
                viewModelScope.launch {
                    _events.emit(ProfileEvent.ShowToast("Profile saved successfully"))
                }
            }
        }
    }
}
