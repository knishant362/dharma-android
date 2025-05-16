package com.aurora.app.ui.screens.cardsList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.app.R
import com.aurora.app.domain.model.spread.FilterItem
import com.aurora.app.domain.repo.TarotRepository
import com.aurora.app.utils.Constants.PACK_NAME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TarotViewModel @Inject constructor(
    private val repository: TarotRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TarotUiState>(TarotUiState.Loading)
    val uiState: StateFlow<TarotUiState> = _uiState

    private val _filters = MutableStateFlow<List<FilterItem>>(emptyList())
    val filters: StateFlow<List<FilterItem>> = _filters

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cards = repository.loadTarotCards(packName = PACK_NAME)
                Timber.e("TarotViewModel: Cards: ${cards.size}")
                _uiState.value = TarotUiState.Success(cards)

                val dynamicFilters = buildList {
                    add("All")
                    addAll(cards.map { it.type }.distinct())
                }
                _filters.value = prepareFilters(dynamicFilters.distinct())

            } catch (e: Exception) {
                _uiState.value = TarotUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    private fun prepareFilters(filters: List<String>): List<FilterItem> {
        return filters.mapIndexed { index, filter ->
            val imagePath = "images/category/${filter.lowercase()}.png"
            Timber.e("TarotViewModel: Filter: $filter, ImagePath: $imagePath")
            FilterItem(id = index.toLong(), title = filter, imagePath = imagePath, description = suitsDescription()[filter.lowercase()] ?: 0)
        }
    }

    private fun suitsDescription(): Map<String, Int> {
        return mapOf(
            "major" to R.string.major_description,
            "minor" to  R.string.minor_description,
            "wands" to R.string.wands_description,
            "cups" to R.string.cups_description,
            "swords" to R.string.swords_description,
            "pentacles" to R.string.pentacles_description
        )
    }


}
