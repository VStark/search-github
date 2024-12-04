package com.sg.ui.search

import androidx.paging.PagingData
import com.sg.data.model.Repo
import kotlinx.coroutines.flow.Flow

sealed class SearchState {
    data object Init : SearchState()
    data object Loading : SearchState()
    data class Success(val paging: Flow<PagingData<Repo>>) : SearchState()
    data class Error(val error: String) : SearchState()
}