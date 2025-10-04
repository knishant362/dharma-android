package com.aurora.app.ui.screens.dasbboard

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.R
import com.aurora.app.data.model.WorkDto
import com.aurora.app.designsystem.theme.Horoscope2
import com.aurora.app.designsystem.theme.Horoscope3
import com.aurora.app.domain.model.dashboard.Featured
import com.aurora.app.domain.model.dashboard.WorkSection
import com.aurora.app.domain.model.dashboard.WorkType
import com.aurora.app.domain.repo.MainRepository
import com.aurora.app.utils.ResponseState
import com.aurora.app.utils.TimeUtil
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val mainRepository: MainRepository,
) : ViewModel() {

    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        fetchDashboardConfig()
        setupUserProfile()
        checkAppVersion()
    }


    private fun fetchDashboardConfig() {
        viewModelScope.launch {
            try {
                val activated = remoteConfig.fetchAndActivate().await()

                Timber.d("Remote config activated: $activated")

                val featured = remoteConfig.getBoolean("dashboard_featured")
                val categories = remoteConfig.getBoolean("dashboard_categories")
                val works = remoteConfig.getBoolean("dashboard_works")

                _uiState.update { current ->
                    current.copy(
                        featuredEnabled = featured,
                        categoriesEnabled = categories,
                        worksEnabled = works,
                        featuredItems = if (featured) getFeaturedDataHindi() else emptyList(),
                        categories = if (categories) getCategories() else emptyList()
                    )
                }

                if (works) fetchWorks()

            } catch (e: Exception) {
                // Fallback to defaults
                _uiState.update { current ->
                    current.copy(
                        featuredEnabled = false,
                        categoriesEnabled = false,
                        worksEnabled = false,
                        featuredItems = emptyList(),
                        categories = emptyList()
                    )
                }
            }
        }
    }


    private fun fetchWorks() {
        viewModelScope.launch(Dispatchers.IO) {
            val posts = mainRepository.getAllPosts()
            Timber.e("Fetched posts: ${posts.size}")

            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val response = mainRepository.fetchWorks()
                Timber.e("Fetched works response: $response")
                when (response) {
                    is ResponseState.Error -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessages = response.message ?: "Error fetching works"
                        )
                    }

                    is ResponseState.Loading -> {}
                    is ResponseState.Success -> {
                        val works = response.data ?: emptyList()
                        Timber.e("Fetched works: ${works.size}")
                        val workSections = works.sortByCategory()
                            .map { (categoryName, works) ->
                                WorkSection(
                                    id = categoryName,
                                    categoryName = categoryName,
                                    works = works
                                )
                            }
                        _uiState.value = _uiState.value.copy(
                            works = works,
                            workSections = workSections,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(errorMessages = e.message ?: "Error fetching works")
            }
        }
    }

    private fun getFeaturedDataHindi(): List<Featured> {
        val date = TimeUtil.getTodayFormatted()
        val featuredData = listOf(
            Featured(
                0,
                date,
                "नए रिंगटोन का संग्रह",
                "सुनिए और डाउनलोड करें",
                R.drawable.bg_halo_1
            ),
            Featured(1, date, "आज के लाइव वॉलपेपर", "अभी देखें", R.drawable.bg_halo_3),
            Featured(2, date, "स्टेटस मेकर", "स्टेटस बनाना शुरू करें", R.drawable.bg_halo_4),
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
//            CategoryItem(
//                "Religious",
//                "Books",
//                R.drawable.ic_book_hindu,
//                listOf(Color(0xFF4ECDC4), Color(0xFF44A08D))
//            ),
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
//            CategoryItem(
//                "VIP",
//                "Plus",
//                R.drawable.ic_temple,
//                listOf(Color(0xFFFB8C00), Color(0xFFFF6F00))
//            )
        )
        return categories
    }


    private fun setupUserProfile() = viewModelScope.launch {
        val userProfile = mainRepository.getUserProfile()
        _uiState.update { it.copy(user = userProfile) }
    }

    private fun checkAppVersion() = viewModelScope.launch {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val localVersion = mainRepository.getDbVersion()
                Timber.e("Local DB version: $localVersion")
                mainRepository.verifyDatabase().collectLatest { response ->
                    when (response) {
                        is ResponseState.Success -> {
                            _uiState.update { it.copy(showUpgradeDialog = false) }
                            Timber.d("Database updated to version ${response.data}")
                        }

                        is ResponseState.Error -> {
                            Timber.e("Database update failed: ${response.message}")
                            _uiState.update { it.copy(showUpgradeDialog = false) }
                        }
                        is ResponseState.Loading -> {
                            _uiState.update { it.copy(showUpgradeDialog = true) }
                        }
                    }
                }

            } catch (e: Exception) {
                Timber.e(e, "Error checking app version: ${e.message}")
                _uiState.update {
                    it.copy(errorMessages = e.message ?: "Error fetching version")
                }
            }
        }
    }

    private fun List<WorkDto>.sortByCategory(): MutableMap<String, MutableList<WorkDto>> {
        try {
            val books = mutableMapOf<String, MutableList<WorkDto>>()
            val otherCategories = setOf("chalisa", "aarti", "mantra", "kavach", "stotram")
            this.forEach { work ->
                val category = if (work.mType == WorkType.BOOK.type) "Books" else {
                    otherCategories.find { work.id.contains(it) } ?: otherCategories.last()
                }
                if (books.containsKey(category)) {
                    books[category]?.add(work)
                } else {
                    books[category] = mutableListOf(work)
                }
            }
            return books
        } catch (e: Exception) {
            Timber.e(e)
            return mutableMapOf()
        }
    }
}