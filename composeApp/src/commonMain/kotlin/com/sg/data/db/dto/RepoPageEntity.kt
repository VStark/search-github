package com.sg.data.db.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repos_page")
data class RepoPageEntity(
    @PrimaryKey @ColumnInfo(name = "repo_id") val repoId: Long,
    @ColumnInfo(index = true) val query: String,
    @ColumnInfo(index = true) val page: Int,
)
