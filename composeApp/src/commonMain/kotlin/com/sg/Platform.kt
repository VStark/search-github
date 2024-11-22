package com.sg

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform