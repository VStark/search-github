package com.sg.data.api.rest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoSimpleOwnerJson(
    @SerialName("login") val login: String,
)
