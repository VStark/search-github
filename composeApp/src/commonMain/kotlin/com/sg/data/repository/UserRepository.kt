package com.sg.data.repository

import com.sg.data.model.User

interface UserRepository {
    val userToken: String
    fun getUser(): User?
    suspend fun initUser(token: String): Result<User>
}