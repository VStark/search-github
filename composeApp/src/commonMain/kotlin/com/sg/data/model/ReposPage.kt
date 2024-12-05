package com.sg.data.model

data class ReposPage(
    val items: List<Repo> = emptyList(),
    val count: Int = 0,
    val page: Int = 1,
    val pages: Int = 1,
    val hasNextPage: Boolean = false,
    val hasPreviousPage: Boolean = false,
    val nextPageUrl: String = "",
    val previousPageUrl: String = "",
)
