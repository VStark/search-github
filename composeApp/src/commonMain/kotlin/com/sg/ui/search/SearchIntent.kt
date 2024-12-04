package com.sg.ui.search

sealed class SearchIntent {
    data class SearchRepository(val query: String) : SearchIntent()
    data class ToggleStar(val nodeId: String, val star: Boolean) : SearchIntent()
}