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
import com.sg.data.model.StarredRepo
import com.sg.data.repository.GithubRepository
import com.sg.data.repository.PagingRepositoryImpl
import com.sg.data.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@ExperimentalPagingApi
@Suppress("OPT_IN_USAGE")
class SearchViewModel(
    private val pagingRepository: PagingRepositoryImpl,
    private val githubRepository: GithubRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val logger = Logger.withTag("SearchViewModel")
    private val _state = MutableStateFlow<SearchState>(SearchState.Init)
    val state = _state.asStateFlow()
    var searchQueryState by mutableStateOf("")
        private set


    init {
        viewModelScope.launch {
            snapshotFlow { searchQueryState }
                .collectLatest {
                    delay(1000)
                    _state.update { SearchState.Loading }
                    if (it.length > 2) {
                        runCatching {
                            pagingRepository.getPagedRepos(
                                searchQueryState,
                                5,
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
                    } else {
                        _state.update { SearchState.Init }
                    }
                }
        }
    }

    fun handleIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.SearchRepository -> {
                searchQueryState = intent.query
            }

            is SearchIntent.ToggleStar -> {
                viewModelScope.launch {
                    githubRepository
                        .setRepoStar(userRepository.token, intent.nodeId, intent.star)
                        .onSuccess {
                            val userId = userRepository.getUser().id
                            if (intent.star) {
                                userRepository.saveStarredRepo(
                                    StarredRepo(
                                        repoId = intent.repoId,
                                        userId = userId
                                    )
                                )
                            } else {
                                userRepository.deleteStarredRepo(intent.repoId)
                            }
                        }
                }
            }
        }
    }
}