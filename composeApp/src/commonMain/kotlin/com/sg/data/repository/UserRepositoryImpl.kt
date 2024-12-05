package com.sg.data.repository

import com.sg.data.model.User

class UserRepositoryImpl private constructor(
    private val githubRepository: GithubRepository,
) : UserRepository {
    private var user: User? = null
    override val userToken: String
        get() = getUser()?.token ?: ""

    override fun getUser(): User? = user

    override suspend fun initUser(token: String): Result<User> =
        githubRepository.getUserInfo(token).onSuccess {
            user = it.copy(token = token)
        }

    companion object {
        val instance by lazy {
            UserRepositoryImpl(GithubRepositoryImpl.instance)
        }
    }
}