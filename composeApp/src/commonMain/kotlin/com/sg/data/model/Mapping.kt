package com.sg.data.model

import com.sg.data.api.rest.RepoJson
import com.sg.data.api.rest.ReposJson
import com.sg.data.api.rest.UserJson
import com.sg.data.db.dto.RepoEntity
import com.sg.data.db.dto.RepoWithUserRepo
import com.sg.data.db.dto.StarredRepoEntity

fun Repo.toEntity(query: String): RepoEntity =
    RepoEntity(
        repoId = id,
        nodeId = nodeId,
        name = name,
        owner = owner,
        query = query,
    )

fun RepoWithUserRepo.toRepo(): Repo =
    Repo(
        id = repo.repoId,
        nodeId = repo.nodeId,
        hasStar = userRepo != null,
        name = repo.name,
        owner = repo.owner,
    )

fun StarredRepo.toEntity(): StarredRepoEntity =
    StarredRepoEntity(
        repoId = repoId,
        userId = userId,
    )

fun UserJson.toUser(): User = run {
    User(
        id = id,
        login = login,
        avatarUrl = avatarUrl,
    )
}

fun ReposJson.toRepos(): ReposPage = run {
    val items = items.map { it.toRepo() }
    ReposPage(
        items = items,
        count = totalCount,
    )
}

fun RepoJson.toRepo(): Repo = run {
    Repo(
        id = id,
        nodeId = nodeId,
        hasStar = false,
        name = name,
        owner = owner.login,
    )
}