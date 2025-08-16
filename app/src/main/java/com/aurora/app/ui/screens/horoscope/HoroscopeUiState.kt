package com.aurora.app.ui.screens.horoscope

import com.aurora.app.data.remote.response.HoroscopeData
import com.aurora.app.data.remote.response.ZodiacSign

data class HoroscopeUiState(
    val isLoading: Boolean = false,
    val selectedZodiacSign: ZodiacSign? = null,
    val horoscopeData: HoroscopeData? = null,
    val zodiacSigns: List<ZodiacSign> = emptyList(),
    val error: String? = null,
    val selectedTab: Int = 0
)
