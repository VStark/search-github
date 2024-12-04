package com.sg.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.cachedIn
import co.touchlab.kermit.Logger
import com.sg.data.repository.PagingRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

@ExperimentalPagingApi
@Suppress("OPT_IN_USAGE")
class SearchViewModel(
    private val pagingRepository: PagingRepositoryImpl,
) : ViewModel() {

    private val logger = Logger.withTag("SearchViewModel")
    private val _state = MutableStateFlow<SearchState>(SearchState.Init)
    val state = _state.asStateFlow()
    var searchQueryState by mutableStateOf("")
        private set

    init {
        snapshotFlow { searchQueryState }
            .filter { it.length > 2 }
            .debounce(1000)
            .map {
                _state.update { SearchState.Loading }
                runCatching {
                    pagingRepository.getPagedRepos(
                        searchQueryState,
                        100
                    ).cachedIn(viewModelScope)
                }.onSuccess { result ->
                    _state.update {
                        SearchState.Success(result)
                    }
                }.onFailure { throwable ->
                    logger.e(throwable) {
                        "Error while searching for repositories"
                    }
                    _state.update {
                        SearchState.Error(throwable.message ?: "Unknown error")
                    }
                }
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