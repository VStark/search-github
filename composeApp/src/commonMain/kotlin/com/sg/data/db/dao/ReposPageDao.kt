package com.sg.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sg.data.db.dto.RepoPageEntity

@Dao
interface ReposPageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RepoPageEntity>)

    @Query("UPDATE repos_page SET page = :page WHERE `query` = :query")
    suspend fun updatePageByQuery(page: Int, query: String)

    @Query("DELETE FROM repos_page")
    suspend fun deleteAll()
}