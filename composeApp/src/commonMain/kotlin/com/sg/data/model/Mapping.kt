package com.sg.data.model

import com.sg.data.api.rest.RepoJson
import com.sg.data.api.rest.ReposJson
import com.sg.data.api.rest.UserJson
import com.sg.data.db.dto.RepoEntity
import com.sg.data.db.dto.RepoPageEntity
import com.sg.data.db.dto.RepoWithUserRepo
import com.sg.data.db.dto.StarredRepoEntity

fun Repo.toEntity(): RepoEntity =
    RepoEntity(
        repoId = id,
        nodeId = nodeId,
        name = name,
        owner = owner,
    )

fun Repo.toRepoPageEntity(query: String, page: Int): RepoPageEntity =
    RepoPageEntity(
        repoId = id,
        query = query,
        page = page,
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

fun UserJson.toUser(): User =
    User(
        id = id,
        login = login,
        avatarUrl = avatarUrl,
    )

fun ReposJson.toRepos(): ReposPage = run {
    val items = items.map { it.toRepo() }
    ReposPage(
        items = items,
        count = totalCount,
    )
}

fun RepoJson.toRepo(): Repo =
    Repo(
        id = id,
        nodeId = nodeId,
        hasStar = false,
        name = name,
        owner = owner.login,
    )
