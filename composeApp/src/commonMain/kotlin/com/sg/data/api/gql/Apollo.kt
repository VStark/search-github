package com.sg.data.api.gql

import co.touchlab.kermit.Logger
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloRequest
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.http.HttpRequest
import com.apollographql.apollo.api.http.HttpResponse
import com.apollographql.apollo.interceptor.ApolloInterceptor
import com.apollographql.apollo.interceptor.ApolloInterceptorChain
import com.apollographql.apollo.network.http.HttpInterceptor
import com.apollographql.apollo.network.http.HttpInterceptorChain
import com.sg.data.repository.UserRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

val apolloClient = ApolloClient.Builder()
    .serverUrl("https://api.github.com/graphql")
    .addHttpInterceptor(AuthorizationInterceptor())
    .addHttpInterceptor(GithubGlobalIdInterceptor())
    .addInterceptor(LoggingApolloInterceptor())
    .build()

class AuthorizationInterceptor : HttpInterceptor {
    override suspend fun intercept(request: HttpRequest, chain: HttpInterceptorChain): HttpResponse {
        val token: String = UserRepositoryImpl.instance.token
        return chain.proceed(request.newBuilder().addHeader("Authorization", "Bearer $token").build())
    }
}

class GithubGlobalIdInterceptor : HttpInterceptor {
    override suspend fun intercept(request: HttpRequest, chain: HttpInterceptorChain): HttpResponse {
        return chain.proceed(request.newBuilder().addHeader("X-Github-Next-Global-ID", "1").build())
    }
}

class LoggingApolloInterceptor : ApolloInterceptor {
    override fun <D : Operation.Data> intercept(request: ApolloRequest<D>, chain: ApolloInterceptorChain): Flow<ApolloResponse<D>> {
        return chain.proceed(request).onEach { response ->
            Logger.i("Received response for ${request.operation.name()}: ${response.data}, exception: ${response.exception}")
        }
    }
}