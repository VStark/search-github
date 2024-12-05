package com.sg.data.repository

import com.sg.data.model.ReposPage
import com.sg.data.model.User

interface GithubRepository {
    suspend fun getReposFromSearch(
        token: String,
        query: String,
        perPage: Int
    ): Result<ReposPage>

    suspend fun getPage(token: String, url: String): Result<ReposPage>
    suspend fun getUserInfo(token: String): Result<User>
}
