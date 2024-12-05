package com.sg.data.api.rest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReposJson (
    @SerialName("total_count") val totalCount: Int,
    @SerialName("items") val items: List<RepoJson>
)