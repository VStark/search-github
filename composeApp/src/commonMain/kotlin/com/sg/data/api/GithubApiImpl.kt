package com.sg.data.api

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Mutation
import com.apollographql.apollo.api.Optional
import com.sg.data.api.gql.apolloClient
import com.sg.data.api.rest.Links
import com.sg.data.api.rest.RepoJson
import com.sg.data.api.rest.ReposJson
import com.sg.data.api.rest.UserJson
import com.sg.data.api.rest.ktorClient
import com.sg.data.model.ReposPage
import com.sg.data.model.User
import com.sg.data.model.toRepos
import com.sg.data.model.toUser
import com.sg.graphql.AddStarMutation
import com.sg.graphql.RemoveStarMutation
import com.sg.graphql.type.AddStarInput
import com.sg.graphql.type.RemoveStarInput
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

class GithubApiImpl(
    private val httpClient: HttpClient,
    private val gqlClient: ApolloClient,
) : GithubApi {

    override suspend fun searchForRepositories(
        token: String,
        query: String,
        perPage: Int,
    ): Result<ReposPage> =
        makeHttpRequest(
            token,
            "https://api.github.com/search/repositories",
            HttpMethod.Get,
            mapOf(
                "q" to query,
                "per_page" to perPage
            )
        ).map {
            prepareRepos(it)
        }

    override suspend fun loadPage(token: String, url: String): Result<ReposPage> =
        makeHttpRequest(token, url, HttpMethod.Get)
            .map {
                prepareRepos(it)
            }

    override suspend fun getUserInfo(token: String): Result<User> =
        makeHttpRequest(token, "https://api.github.com/user", HttpMethod.Get)
            .map {
                it.body<UserJson>()
                    .toUser()
            }

    override suspend fun getUserStarredRepos(token: String): Result<Map<Long, String>> =
        makeHttpRequest(token, "https://api.github.com/user/starred", HttpMethod.Get)
            .map { resp ->
                resp.body<List<RepoJson>>()
                    .associate {
                        it.id to it.nodeId
                    }
            }

    override suspend fun setRepoStar(
        token: String,
        repoId: String,
        starred: Boolean
    ): Result<Unit>  {
        return makeGQLMutation(
            if (starred) {
                AddStarMutation(
                    AddStarInput(
                        clientMutationId = Optional.present("123"),
                        starrableId = repoId
                    )
                )
            } else {
                RemoveStarMutation(
                    RemoveStarInput(
                        clientMutationId = Optional.present("321"),
                        starrableId = repoId
                    )
                )
            }
        )
    }


    private suspend fun prepareRepos(httpResponse: HttpResponse): ReposPage {
        val links = parseLinks(httpResponse.headers[HttpHeaders.Link])
        val repos = httpResponse.body<ReposJson>().toRepos()
        val page = httpResponse.request.url.parameters["page"]?.toIntOrNull() ?: 1
        val pages = links.last?.let { Url(links.last).parameters["page"]?.toIntOrNull() ?: 1 } ?: 1
        return repos.copy(
            page = page,
            pages = pages,
            nextPageUrl = links.next,
            previousPageUrl = links.prev,
            hasNextPage = !links.next.isNullOrEmpty(),
            hasPreviousPage = !links.prev.isNullOrEmpty(),
        )
    }

    private fun parseLinks(links: String?): Links {
        return links?.let {
            val matchResult = "<(?<url>.+?)>;\\s*rel=\"(?<value>.+?)\"".toRegex().findAll(links)
            var linksTmp = Links()
            matchResult.forEach {
                val (url, rel) = it.destructured
                when (rel) {
                    "next" -> linksTmp = linksTmp.copy(next = url)
                    "prev" -> linksTmp = linksTmp.copy(prev = url)
                    "first" -> linksTmp = linksTmp.copy(first = url)
                    "last" -> linksTmp = linksTmp.copy(last = url)
                    else -> {}
                }
            }
            linksTmp
        } ?: Links()
    }

    private suspend fun makeGQLMutation(
        mutation: Mutation<*>
    ): Result<Unit> =
        runCatching {
            gqlClient.mutation(mutation).execute()
        }.fold(
            onSuccess = { response ->
                return when {
                    response.exception != null -> {
                        Result.failure(
                            Exception(
                                "Failed to mutate data, name: ${response.operation.name()}",
                                response.exception?.cause,
                            )
                        )
                    }

                    response.hasErrors() -> {
                        Result.failure(
                            Exception(
                                "Failed to mutate data, name: ${response.operation.name()}, " +
                                        "errors: ${response.errors?.map { it.message }}",
                            )
                        )
                    }

                    else -> Result.success(Unit)
                }
            },
            onFailure = {
                Result.failure(it)
            }
        )

    private suspend fun makeHttpRequest(
        token: String,
        url: String,
        method: HttpMethod,
        params: Map<String, Any> = emptyMap()
    ): Result<HttpResponse> =
        runCatching {
            httpClient.prepareRequest(url) {
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
        }.fold(
            onSuccess = { httpResponse ->
                return@fold when {
                    httpResponse.status.isSuccess() -> {
                        Result.success(httpResponse)
                    }

                    httpResponse.status == HttpStatusCode.Forbidden -> {
                        Result.failure(Exception("Invalid token."))
                    }

                    else -> {
                        Result.failure(Exception("Unknown error."))
                    }
                }
            },
            onFailure = {
                Result.failure(it)
            }
        )

    companion object {
        val instance by lazy { GithubApiImpl(ktorClient, apolloClient) }
    }
}