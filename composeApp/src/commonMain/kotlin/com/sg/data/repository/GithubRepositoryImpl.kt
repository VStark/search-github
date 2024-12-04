package com.sg.data.repository

import com.sg.data.api.RemoteApi
import com.sg.data.api.RemoteApiImpl
import com.sg.data.model.User
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GithubRepositoryImpl private constructor(
    private val api: RemoteApi,
): GithubRepository {

    override suspend fun getUserInfo(token: String): Result<User> =
        coroutineScope {
            val userDeffer = async {
                api.getUserInfo(token)
            }
            val reposDeffer = async {
                api.getUserStarredRepos(token)
            }
            val userResult = userDeffer.await().getOrNull()
            val starredReposResult = reposDeffer.await().getOrNull()

            return@coroutineScope userResult?.let { user ->
                starredReposResult?.let { starredRepos ->
                    Result.success(user.copy(starredRepos = starredRepos))
                }
            } ?: Result.failure(Exception("Failed to get user."))
        }

    companion object {
        val instance by lazy { GithubRepositoryImpl(RemoteApiImpl.instance) }
    }
}
