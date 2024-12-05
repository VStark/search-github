package com.sg.data.api.rest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoJson(
    @SerialName("id") val id: Long,
    @SerialName("node_id") val nodeId: String,
    @SerialName("name") val name: String,
    @SerialName("owner") val owner: RepoSimpleOwnerJson,
)