package com.sg.data.model

import com.sg.data.api.rest.RepoJson
import com.sg.data.api.rest.ReposJson
import com.sg.data.api.rest.UserJson

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