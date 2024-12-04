package com.sg.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sg.data.db.dto.RepoEntity

@Dao
interface RepoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(queries: List<RepoEntity>)

    @Query("SELECT * FROM repos WHERE `query` LIKE :query ORDER BY id ASC")
    fun pagingSource(query: String): PagingSource<Int, RepoEntity>

    @Query("DELETE FROM repos WHERE `query` = :query")
    suspend fun deleteByQuery(query: String)
}