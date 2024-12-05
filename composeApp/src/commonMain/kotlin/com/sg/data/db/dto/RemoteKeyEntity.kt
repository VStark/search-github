package com.sg.data.db.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    @PrimaryKey val query: String,
    @ColumnInfo(name = "next_key") val nextKey: String?,
)
