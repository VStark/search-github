package com.sg.data.repository

import com.sg.data.api.GithubApi
import com.sg.data.model.ReposPage
import com.sg.data.model.User

class GithubApiMock(
    private val reposPage: ReposPage = ReposPage(),
    private val searchForRepositoriesSuccess: Boolean = true,
    private val loadPageSuccess: Boolean = true,
    private val user: User = User(),
    private val getUserInfoSuccess: Boolean = true,
    private val starredRepos: Map<Long, String> = mapOf(),
    private val getUserStarredReposSuccess: Boolean = true,
    private val setRepoStarSuccess: Boolean = true,
) : GithubApi {
    override suspend fun searchForRepositories(
        token: String,
        query: String,
        perPage: Int
    ): Result<ReposPage> =
        if (searchForRepositoriesSuccess) {
            Result.success(reposPage)
        } else {
            Result.failure(Exception())
        }

    override suspend fun loadPage(token: String, url: String): Result<ReposPage> =
        if (loadPageSuccess) {
            Result.success(reposPage)
        } else {
            Result.failure(Exception())
        }

    override suspend fun getUserInfo(token: String): Result<User> =
        if (getUserInfoSuccess) {
            Result.success(user)
        } else {
            Result.failure(Exception())
        }

    override suspend fun getUserStarredRepos(token: String): Result<Map<Long, String>> =
        if (getUserStarredReposSuccess) {
            Result.success(starredRepos)
        } else {
            Result.failure(Exception())
        }

    override suspend fun setRepoStar(
        token: String,
        repoId: String,
        starred: Boolean
    ): Result<Unit> =
        if (setRepoStarSuccess) {
            Result.success(Unit)
        } else {
            Result.failure(Exception())
        }
}