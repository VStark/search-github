package com.sg.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow

@Suppress("OPT_IN_USAGE")
class SearchViewModel(
) : ViewModel() {

    private val _state = MutableStateFlow<SearchState>(SearchState.Init)
    val state = _state.asStateFlow()
    var searchQueryState by mutableStateOf("")
        private set

    init {
        snapshotFlow { searchQueryState }
            .filter { it.length > 2 }
            .debounce(1000)
            .map {
            }.launchIn(viewModelScope)
    }

    fun handleIntent(searchIntent: SearchIntent) {
        when (searchIntent) {
            is SearchIntent.SearchRepository -> {
                searchQueryState = searchIntent.query
            }

            is SearchIntent.ToggleStar -> {
            }
        }
    }
}