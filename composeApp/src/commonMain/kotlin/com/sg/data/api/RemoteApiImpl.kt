package com.sg.data.api

import co.touchlab.kermit.Logger
import com.sg.data.api.rest.Links
import com.sg.data.api.rest.RepoJson
import com.sg.data.api.rest.ReposJson
import com.sg.data.api.rest.UserJson
import com.sg.data.api.rest.ktorClient
import com.sg.data.model.User
import com.sg.data.model.toUser
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.prepareRequest
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.isSuccess
import io.ktor.http.parameters

class RemoteApiImpl(
    private val httpClient: HttpClient,
) : RemoteApi {

    override suspend fun getUserInfo(token: String): Result<User> {
        val httpResponse =
            makeRequest(
                token,
                "https://api.github.com/user",
                HttpMethod.Get
            )

        return when {
            httpResponse.status.isSuccess() -> {
                Result.success(httpResponse.body<UserJson>().toUser())
            }

            httpResponse.status == HttpStatusCode.Forbidden -> {
                Result.failure(Exception("Invalid token."))
            }

            else -> {
                Result.failure(Exception("Unknown error."))
            }
        }
    }

    override suspend fun getUserStarredRepos(token: String): Result<Map<Long, String>> {
        val httpResponse =
            makeRequest(
                token,
                "https://api.github.com/user/starred",
                HttpMethod.Get
            )

        return when {
            httpResponse.status.isSuccess() -> {
                val result = httpResponse.body<List<RepoJson>>().associate { it.id to it.nodeId }
                Logger.i("getUserStarredRepos: $result")
                Result.success(result)
            }

            httpResponse.status == HttpStatusCode.Forbidden -> {
                Result.failure(Exception("Invalid token."))
            }

            else -> {
                Result.failure(Exception("Unknown error."))
            }
        }
    }

    private suspend fun makeRequest(
        token: String,
        url: String,
        method: HttpMethod,
        params: Map<String, Any> = emptyMap()
    ): HttpResponse {
        return httpClient.prepareRequest(url) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
                append(HttpHeaders.Accept, "application/vnd.github+json")
                append("X-Github-Next-Global-ID", "1")
            }
            if (params.isNotEmpty()) {
                parameters {
                    params.forEach { (key, value) ->
                        parameter(key, value)
                    }
                }
            }
            this.method = method
        }.execute()
    }

    companion object {
        val instance by lazy { RemoteApiImpl(ktorClient) }
    }
}