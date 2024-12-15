package com.sg.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sg.data.db.dto.StarredRepoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StarredReposDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(starredRepo: StarredRepoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(starredRepos: List<StarredRepoEntity>)

    @Query("SELECT count(*) FROM starred_repos")
    fun watch(): Flow<Int>

    @Query("DELETE FROM starred_repos")
    suspend fun deleteAll()

    @Query("DELETE FROM starred_repos WHERE repo_id = :repoId")
    suspend fun deleteByRepoId(repoId: Long)
}