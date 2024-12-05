package com.sg.data.repository

import com.sg.data.model.StarredRepo
import com.sg.data.model.User

interface UserRepository {
    val token: String
    fun getUser(): User
    suspend fun initUser(token: String): Result<User>
    suspend fun saveStarredRepo(starredRepo: StarredRepo): Result<Unit>
    suspend fun deleteStarredRepo(repoId: Long): Result<Unit>
}