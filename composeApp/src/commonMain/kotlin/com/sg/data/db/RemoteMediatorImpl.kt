package com.sg.data.db

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import co.touchlab.kermit.Logger
import com.sg.data.db.dto.RemoteKeyEntity
import com.sg.data.db.dto.RepoWithUserRepo
import com.sg.data.model.toEntity
import com.sg.data.repository.GithubRepository

@OptIn(ExperimentalPagingApi::class)
class RemoteMediatorImpl(
    private val query: String,
    private val db: AppDatabase,
    private val perPage: Int,
    private val token: String,
    private val repo: GithubRepository,
) : RemoteMediator<Int, RepoWithUserRepo>() {
    private val logger = Logger.withTag("RemoteMediatorImpl")
    private val repoDao = db.reposDao()
    private val remoteKeyDao = db.remoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RepoWithUserRepo>
    ): MediatorResult {
        return try {
            /*
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND ->
                    return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                LoadType.APPEND -> {
                    val remoteKey = remoteKeyDao.getByQuery(query)
                    if (remoteKey.nextKey == null) {
                        return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    }

                    remoteKey.nextKey
                }
            }

            val result = if (loadKey == null) {
                repo.getReposFromSearch(token, query, perPage)
            } else {
                repo.getPage(token, loadKey)
            }

            val repoPage = result.getOrElse {
                return MediatorResult.Error(it)
            }

            if (loadType == LoadType.REFRESH) {
                repoDao.deleteByQuery(query)
                remoteKeyDao.deleteByQuery(query)
            }
            repoDao.insertAll(repoPage.items.map { it.toEntity(query) })
            remoteKeyDao.insert(RemoteKeyEntity(query, repoPage.nextPageUrl))


             */
            return MediatorResult.Success(
                endOfPaginationReached = true //repoPage.nextPageUrl.isEmpty()
            )
        } catch (e: Exception) {
            logger.e(e) { "Load method error: ${e.message}" }
            MediatorResult.Error(e)
        }
    }
}