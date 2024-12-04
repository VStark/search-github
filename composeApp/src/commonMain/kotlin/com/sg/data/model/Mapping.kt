package com.sg.data.model

import com.sg.data.api.rest.UserJson

fun UserJson.toUser(): User = run {
    User(
        id = id,
        login = login,
        avatarUrl = avatarUrl,
    )
}
