package com.aurora.app.ui.screens.profile

data class ProfileUIState(
    val name: String = "",
    val gender: String = "",
    val dateOfBirth: String = "",
    val relationshipStatus: String = "",
    val occupation: String = "",
    val errors: Map<String, String?> = emptyMap()
)