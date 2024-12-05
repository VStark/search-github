package com.sg.ui

import SearchGithub
import TokenLogin
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.ExperimentalPagingApi
import com.sg.data.repository.GithubRepositoryImpl
import com.sg.data.repository.PagingRepositoryImpl
import com.sg.data.repository.UserRepositoryImpl
import com.sg.ui.search.SearchScreen
import com.sg.ui.search.SearchViewModel
import com.sg.ui.token.TokenScreen
import com.sg.ui.token.TokenViewModel

@ExperimentalPagingApi
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = TokenLogin,
        modifier = modifier.fillMaxSize()
    ) {
        composable<TokenLogin> {
            val viewModel = viewModel { TokenViewModel(UserRepositoryImpl.instance) }
            val state by viewModel.state.collectAsState()

            TokenScreen(
                sendIntent = viewModel::handleIntent,
                navigate = {
                    navController.navigate(SearchGithub)
                },
                state = state,
                modifier = modifier,
            )
        }
        composable<SearchGithub> {
            val viewModel = viewModel {
                SearchViewModel(
                    PagingRepositoryImpl.instance,
                    GithubRepositoryImpl.instance,
                    UserRepositoryImpl.instance
                )
            }
            val state by viewModel.state.collectAsState()

            SearchScreen(
                searchQueryState = viewModel.searchQueryState,
                state = state,
                sendIntent = viewModel::handleIntent,
                modifier = modifier,
            )
        }
    }
}
