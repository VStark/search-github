package com.sg.data.repository

import com.sg.data.model.User

interface GithubRepository {
    suspend fun getUserInfo(token: String): Result<User>
}
