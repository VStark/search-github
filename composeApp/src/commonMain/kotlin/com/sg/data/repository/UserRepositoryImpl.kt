package com.sg.data.repository

import com.sg.data.db.appDatabase
import com.sg.data.db.dao.StarredReposDao
import com.sg.data.model.StarredRepo
import com.sg.data.model.User
import com.sg.data.model.toEntity

class UserRepositoryImpl private constructor(
    private val githubRepository: GithubRepository,
    private val starredReposDao: StarredReposDao,
) : UserRepository {
    private lateinit var user: User
    override val token: String
        get() = user.token

    override fun getUser(): User = user

    override suspend fun initUser(token: String): Result<User> {
        return githubRepository.getUserInfo(token).onSuccess {
            user = it.copy(token = token)
        }
    }

    override suspend fun saveStarredRepo(
        starredRepo: StarredRepo
    ): Result<Unit> =
        runCatching {
            starredReposDao.insert(
                starredRepo.toEntity()
            )
        }

    override suspend fun deleteStarredRepo(repoId: Long): Result<Unit> =
        runCatching {
            starredReposDao.deleteByRepoId(repoId)
        }

    companion object {
        val instance by lazy {
            UserRepositoryImpl(
                GithubRepositoryImpl.instance,
                appDatabase.starredReposDao(),
            )
        }
    }
}