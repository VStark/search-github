package com.sg.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sg.data.db.dto.StarredRepoEntity

@Dao
interface StarredReposDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(starredRepo: StarredRepoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(starredRepos: List<StarredRepoEntity>)

    @Query("DELETE FROM starred_repos")
    suspend fun deleteAll()

    @Query("DELETE FROM starred_repos WHERE repo_id = :repoId")
    suspend fun deleteByRepoId(repoId: Long)
}