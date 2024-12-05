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
interface RepoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(queries: List<RepoEntity>)

    @Transaction
    @Query("SELECT * FROM repos WHERE `query` LIKE :query ORDER BY id ASC")
    fun pagingSource(query: String): PagingSource<Int, RepoWithUserRepo>

    @Query("DELETE FROM repos WHERE `query` = :query")
    suspend fun deleteByQuery(query: String)
}