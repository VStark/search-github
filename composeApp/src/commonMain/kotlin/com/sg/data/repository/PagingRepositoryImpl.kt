package com.sg.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.map
import com.sg.data.db.AppDatabase
import com.sg.data.db.RemoteMediatorImpl
import com.sg.data.db.appDatabase
import com.sg.data.model.Repo
import com.sg.data.model.toRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
                initialLoadSize = perPage,
                enablePlaceholders = false
            ),
            remoteMediator = RemoteMediatorImpl(
                query,
                db,
                perPage,
                userRepository.token,
                githubRepository
            )
        ) {
            db.reposDao().pagingSource(query)
        }.flow.map {
            it.map { entity ->
                entity.toRepo()
            }
        }

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