package com.sg.data.db.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "starred_repos")
data class StarredRepoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "repo_id", index = true) val repoId: Long = 0,
    @ColumnInfo(name = "user_id", index = true) val userId: Long = 0,
)