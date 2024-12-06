package com.sg.data.api.rest

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val ktorClient = HttpClient {
    install(ContentNegotiation) {
        val json = Json {
            ignoreUnknownKeys = true
        }
        json(json)
    }
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                co.touchlab.kermit.Logger.withTag("ktor").v(message)
            }
        }
        level = LogLevel.INFO
        sanitizeHeader {
            it == HttpHeaders.Authorization
        }
    }
}