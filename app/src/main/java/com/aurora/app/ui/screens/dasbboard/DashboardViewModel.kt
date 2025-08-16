package com.aurora.app.ui.screens.dasbboard

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.R
import com.aurora.app.designsystem.theme.Horoscope2
import com.aurora.app.designsystem.theme.Horoscope3
import com.aurora.app.domain.model.dashboard.Featured
import com.aurora.app.domain.model.dashboard.WorkSection
import com.aurora.app.domain.repo.MainRepository
import com.aurora.app.utils.ResponseState
import com.aurora.app.utils.TimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val mainRepository: MainRepository,
) : ViewModel() {


    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        setupDashboard()
        fetchWorks()
    }

    private fun fetchWorks() {
        viewModelScope.launch(Dispatchers.IO) {
            val posts = mainRepository.getAllPosts()
            Timber.e("Fetched posts: ${posts.size}")

            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val response = mainRepository.fetchWorks()
                Timber.e("Fetched works response: $response")
                when(response) {
                    is ResponseState.Error -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessages = response.message ?: "Error fetching works"
                        )
                    }
                    is ResponseState.Loading -> {}
                    is ResponseState.Success -> {
                        val works = response.data ?: emptyList()
                        Timber.e("Fetched works: ${works.size}")
                        val workSections = works.groupBy { it.category }
                            .map { (categoryName, works) ->
                                WorkSection(
                                    id = categoryName,
                                    categoryName = categoryName,
                                    works = works
                                )
                            }
                        _uiState.value = _uiState.value.copy(works = works, workSections = workSections, isLoading = false)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessages = e.message ?: "Error fetching works")
            }
        }
    }

    private fun getFeaturedDataHindi(): List<Featured> {
        val date = TimeUtil.getTodayFormatted()
        val featuredData = listOf(
            Featured(0, date, "नए रिंगटोन का संग्रह", "सुनिए और डाउनलोड करें", R.drawable.bg_halo_1),
            Featured(1, date, "आज के लाइव वॉलपेपर", "अभी देखें", R.drawable.bg_halo_3),
            Featured(2, date, "पुस्तक की सिफारिश", "पढ़ना शुरू करें", R.drawable.bg_halo_4),
        )
        return featuredData
    }

    private fun getCategories(): List<CategoryItem> {
        val categories = listOf(
            CategoryItem(
                "Ringtone &",
                "Aartis",
                R.drawable.ic_anahata,
                listOf(Color(0xFFFF6B6B), Color(0xFFFF8E8E), Color(0xFFFFA8A8))
            ),
            CategoryItem(
                "Religious",
                "Books",
                R.drawable.ic_book_hindu,
                listOf(Color(0xFF4ECDC4), Color(0xFF44A08D))
            ),
            CategoryItem(
                "Wallpapers",
                "Hindu",
                R.drawable.ic_namaste,
                listOf(Color(0xFF45B7D1), Color(0xFF96C93D))
            ),
            CategoryItem(
                "Set",
                "Status",
                R.drawable.ic_swastika,
                listOf(Color(0xFFF093FB), Color(0xFFF5576C))
            ),
            CategoryItem(
                "Daily",
                "Horoscope",
                R.drawable.ic_trishul,
                listOf(Horoscope2, Horoscope3)
            ),
            CategoryItem(
                "VIP",
                "Plus",
                R.drawable.ic_temple,
                listOf(Color(0xFFFB8C00), Color(0xFFFF6F00))
            )
        )
        return categories
    }

    private fun setupDashboard() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userProfile = mainRepository.getUserProfile()
                _uiState.value = DashboardUiState(
                    featuredItems = getFeaturedDataHindi(),
                    categories = getCategories(),
                    user = userProfile
                )
            } catch (e: Exception) {
                _uiState.value = DashboardUiState(errorMessages = e.message ?: "Error loading spreads")
            }
        }
    }

}