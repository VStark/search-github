package com.sg.data.model

data class Repo(
    val id: Long = 0,
    val nodeId: String = "",
    val hasStar: Boolean = false,
    val name: String = "",
    val owner: String = "",
)
