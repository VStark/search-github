package com.sg.data.api

import com.sg.data.model.User

interface RemoteApi {
    suspend fun getUserInfo(token: String): Result<User>
    suspend fun getUserStarredRepos(token: String): Result<Map<Long, String>>
}