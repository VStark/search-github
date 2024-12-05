package com.sg.data.repository

import com.sg.data.api.GithubApi
import com.sg.data.api.GithubApiImpl
import com.sg.data.db.appDatabase
import com.sg.data.db.dao.StarredReposDao
import com.sg.data.db.dto.StarredRepoEntity
import com.sg.data.model.ReposPage
import com.sg.data.model.User
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GithubRepositoryImpl(
    private val api: GithubApi,
    private val starredReposDao: StarredReposDao,
) : GithubRepository {

    override suspend fun getReposFromSearch(
        token: String,
        query: String,
        perPage: Int
    ): Result<ReposPage> =
        api.searchForRepositories(token, query, perPage)

    override suspend fun getPage(token: String, url: String): Result<ReposPage> =
        api.loadPage(token, url)

    override suspend fun getUserInfo(token: String): Result<User> =
        coroutineScope {
            val userDeffer = async {
                api.getUserInfo(token)
            }
            val reposDeffer = async {
                api.getUserStarredRepos(token)
            }

            val userResult = userDeffer.await()
            val reposResult = reposDeffer.await()

            return@coroutineScope runCatching {
                val user = userResult.getOrThrow()
                val repos = reposResult.getOrThrow()
                starredReposDao.deleteAll()
                starredReposDao.insertAll(
                    repos.keys.map { repoId ->
                        StarredRepoEntity(
                            repoId = repoId,
                            userId = user.id,
                        )
                    })

                user
            }
        }

    override suspend fun setRepoStar(
        token: String,
        repoId: String,
        starred: Boolean
    ): Result<Unit> =
        api.setRepoStar(token, repoId, starred)

    companion object {
        val instance by lazy {
            GithubRepositoryImpl(
                GithubApiImpl.instance,
                appDatabase.starredReposDao(),
            )
        }
    }
}
