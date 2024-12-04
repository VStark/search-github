package com.sg.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sg.data.db.dto.RemoteKeyEntity

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(remoteKey: RemoteKeyEntity)

    @Query("SELECT * FROM remote_keys WHERE `query` = :query")
    suspend fun getByQuery(query: String): RemoteKeyEntity

    @Query("DELETE FROM remote_keys WHERE `query` = :query")
    suspend fun deleteByQuery(query: String)
}