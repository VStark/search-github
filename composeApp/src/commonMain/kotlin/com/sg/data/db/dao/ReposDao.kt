package com.sg.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sg.data.db.dto.RepoEntity
import com.sg.data.db.dto.RepoWithUserRepo

@Dao
interface ReposDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(queries: List<RepoEntity>)

    @Transaction
    @Query("SELECT * FROM repos " +
            "JOIN repos_page ON repos.repo_id = repos_page.repo_id " +
            "WHERE repos_page.`query` = :query " +
            "AND repos_page.page = :page " +
            "ORDER BY id ASC")
    suspend fun readByQueryAndPage(query: String, page: Int): List<RepoWithUserRepo>

    @Query("DELETE FROM repos")
    suspend fun deleteAll()

    @Transaction
    @Query("SELECT * FROM repos " +
            "JOIN repos_page ON repos.repo_id = repos_page.repo_id " +
            "WHERE repos_page.`query` = :query " +
            "ORDER BY id ASC")
    fun pagingSource(query: String): PagingSource<Int, RepoWithUserRepo>
}