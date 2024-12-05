package com.sg.data.db.dto

import androidx.room.Embedded
import androidx.room.Relation

data class RepoWithUserRepo(
    @Embedded
    val repo: RepoEntity,
    @Relation(
        parentColumn = "repo_id",
        entityColumn = "repo_id"
    )
    val userRepo: StarredRepoEntity?
)