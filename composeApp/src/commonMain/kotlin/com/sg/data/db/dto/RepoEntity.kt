package com.sg.data.db.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repos")
data class RepoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "repo_id") val repoId: Long = 0,
    @ColumnInfo(name = "node_id") val nodeId: String = "",
    val name: String = "",
    val owner: String = "",
    @ColumnInfo(index = true) val query: String = "",
)