package com.sg

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.paging.ExperimentalPagingApi
import com.sg.ui.MainScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalPagingApi::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        MainScreen()
    }
}