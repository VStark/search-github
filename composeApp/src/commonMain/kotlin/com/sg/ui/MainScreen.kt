package com.sg.ui

import SearchGithub
import TokenLogin
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.ExperimentalPagingApi
import com.sg.ui.search.SearchScreen
import com.sg.ui.token.TokenScreen

@ExperimentalPagingApi
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = TokenLogin,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        composable<TokenLogin> {

            TokenScreen(
                modifier = modifier.fillMaxSize()
            )
        }
        composable<SearchGithub> {

            SearchScreen(
                modifier = modifier.fillMaxSize(),
            )
        }
    }
}
