package com.sg.data.repository

import com.sg.data.db.dao.StarredReposDao
import com.sg.data.db.dto.StarredRepoEntity
import kotlinx.coroutines.flow.Flow

class StarredRepositoryMock(
    private val insertThrowException: Boolean = false,
    private val insertAllThrowException: Boolean = false,
    private val deleteAllThrowException: Boolean = false,
    private val deleteByRepoIdThrowException: Boolean = false,
) : StarredReposDao {
    override suspend fun insert(starredRepo: StarredRepoEntity) =
        if (insertThrowException) {
            throw Exception()
        } else {
            Unit
        }

    override suspend fun insertAll(starredRepos: List<StarredRepoEntity>) =
        if (insertAllThrowException) {
            throw Exception()
        } else {
            Unit
        }

    override fun watch(): Flow<Int> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAll() =
        if (deleteAllThrowException) {
            throw Exception()
        } else {
            Unit
        }

    override suspend fun deleteByRepoId(repoId: Long) =
        if (deleteByRepoIdThrowException) {
            throw Exception()
        } else {
            Unit
        }
}