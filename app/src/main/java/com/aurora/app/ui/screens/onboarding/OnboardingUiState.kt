package com.aurora.app.ui.screens.onboarding

data class OnboardingUiState(
    val totalSteps: Int = 5,
    val currentStep: Int = 0,
    val name: String = "",
    val dateOfBirth: String = "",
    val genderOptions: List<String> = listOf("MALE", "FEMALE", "NON-BINARY"),
    val gender: String= ""
)