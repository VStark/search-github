package com.sg.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import co.touchlab.kermit.Logger
import com.sg.data.db.AppDatabase
import com.sg.data.db.appDatabase
import com.sg.data.db.dto.RemoteKeyEntity
import com.sg.data.model.Repo
import com.sg.data.model.ReposPage
import com.sg.data.model.toEntity
import com.sg.data.model.toRepo
import com.sg.data.model.toRepoPageEntity
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@ExperimentalPagingApi
class PagingRepositoryImpl(
    private val userRepository: UserRepository,
    private val githubRepository: GithubRepository,
    private val db: AppDatabase,
) : PagingRepository {
    override fun getPagedRepos(query: String, perPage: Int): Flow<PagingData<Repo>> =
        Pager(
            config = PagingConfig(
                pageSize = perPage,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                PagingSourceImpl(
                    githubRepository,
                    db,
                    userRepository.token,
                    query,
                    perPage,
                )
            }
        ).flow

    companion object {
        val instance by lazy {
            PagingRepositoryImpl(
                UserRepositoryImpl.instance,
                GithubRepositoryImpl.instance,
                appDatabase
            )
        }
    }
}

private class PagingSourceImpl(
    private val githubRepository: GithubRepository,
    private val db: AppDatabase,
    private val token: String,
    private val query: String,
    private val perPage: Int
) : PagingSource<Int, Repo>() {
    val logger = Logger.withTag("PagingSourceImpl")
    val reposDao = db.reposDao()
    val reposPageDao = db.reposPageDao()
    val remoteKeysDao = db.remoteKeysDao()
    val watchStarredRepos: Flow<Int> = db.starredReposDao().watch()
    val initialized = AtomicBoolean(false)

    init {
        val job = CoroutineScope(Dispatchers.IO).launch {
            watchStarredRepos
                .distinctUntilChanged()
                .collect {
                    if (initialized.getAndSet(true)) {
                        invalidate()
                    }
                }
        }
        registerInvalidatedCallback { job.cancel() }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repo> {
        return try {
            logger.i { "Page: ${params.key}, loadSize: ${params.loadSize}, thread: ${Thread.currentThread().name}" }
            val page = params.key
            val hasNextPage: Boolean

            val repos = when (page) {
                // completely new search
                null -> {
                    db.useWriterConnection { trans ->
                        trans.immediateTransaction {
                            reposDao.deleteAll()
                            reposPageDao.deleteAll()
                            remoteKeysDao.deleteAll()
                        }
                    }

                    val reposPage = githubRepository
                        .getReposFromSearch(token, query, perPage)
                        .getOrNull()
                        ?: throw Exception("Error on source")
                    val tmpPage = 1 // don't use null, different query is needed
                    insertAll(reposPage, query, tmpPage)
                    hasNextPage = reposPage.hasNextPage
                    reposDao.readByQueryAndPage(query, tmpPage)
                }
                // refreshed search, look at getRefreshKey()
                FIRST_PAGE -> {
                    hasNextPage = remoteKeysDao.getRemoteKeyByQuery(query).nextKey != null
                    reposPageDao.updatePageByQuery(FIRST_PAGE, query)
                    reposDao.readByQueryAndPage(query, page)
                }
                else -> {
                    val key = remoteKeysDao.getRemoteKeyByQuery(query)
                    val reposPage = githubRepository
                        .getPage(token, key.nextKey!!)
                        .getOrNull()
                        ?: throw Exception("Error when getting remote page")
                    insertAll(reposPage, query, page)
                    hasNextPage = reposPage.hasNextPage
                    reposDao.readByQueryAndPage(query, page)
                }
            }

            logger.i { "repos: ${repos.size}, hasNextPage: $hasNextPage" }
            LoadResult.Page(
                repos.map { entity -> entity.toRepo() },
                null,
                if (hasNextPage) page?.plus(1) ?: FIRST_PAGE.plus(1) else null
            )
        } catch (e: Exception) {
            logger.e(e) { "Error while loading data" }
            LoadResult.Error(e)
        }
    }

    private suspend fun insertAll(
        reposPage: ReposPage,
        query: String,
        page: Int,
    ) {
        db.useWriterConnection { trans ->
            trans.immediateTransaction {
                reposDao.insertAll(reposPage.items.map { it.toEntity() })
                reposPageDao.insertAll(reposPage.items.map { it.toRepoPageEntity(query, page) })
                remoteKeysDao.insert(RemoteKeyEntity(query, reposPage.nextPageUrl))
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Repo>): Int {
        return FIRST_PAGE
    }

    companion object {
        const val FIRST_PAGE = 1
    }
}