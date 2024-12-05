package com.sg.data.repository

import com.sg.data.model.ReposPage
import com.sg.data.model.User

class GithubRepositoryMock(
    private val testUser: User,
    private val getUserInfoSuccess: Boolean = true,
    private val getPageSuccess: Boolean = true,
    private val getReposFromSearchSuccess: Boolean = true,
    private val setRepoStarSuccess: Boolean = true,
) : GithubRepository {
    override suspend fun getReposFromSearch(
        token: String,
        query: String,
        perPage: Int
    ): Result<ReposPage> {
        return if (getReposFromSearchSuccess) {
            Result.success(ReposPage())
        } else {
            Result.failure(Exception())
        }
    }

    override suspend fun getPage(token: String, url: String): Result<ReposPage> {
        return if (getPageSuccess) {
            Result.success(ReposPage())
        } else {
            Result.failure(Exception())
        }
    }

    override suspend fun getUserInfo(token: String): Result<User> {
        return if (getUserInfoSuccess) {
            Result.success(testUser)
        } else {
            Result.failure(Exception())
        }
    }

    override suspend fun setRepoStar(
        token: String,
        repoId: String,
        starred: Boolean
    ): Result<Unit> {
        return if (setRepoStarSuccess) {
            Result.success(Unit)
        } else {
            Result.failure(Exception())
        }
    }
}