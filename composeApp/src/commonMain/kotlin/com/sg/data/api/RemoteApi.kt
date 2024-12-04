package com.sg.data.api

import com.sg.data.model.ReposPage
import com.sg.data.model.User

interface RemoteApi {
    suspend fun searchForRepositories(token: String, query: String, perPage: Int): Result<ReposPage>
    suspend fun loadPage(token: String, url: String): Result<ReposPage>
    suspend fun getUserInfo(token: String): Result<User>
    suspend fun getUserStarredRepos(token: String): Result<Map<Long, String>>
}