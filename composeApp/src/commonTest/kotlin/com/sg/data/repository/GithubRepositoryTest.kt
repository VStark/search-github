package com.sg.data.repository

import com.sg.data.model.User
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

class GithubRepositoryTest {
    companion object {
        lateinit var githubRepository: GithubRepository
    }

    @Test
    fun getUserInfo_AllApiAndDbCallsWillBeSuccessful_GetExpectedUser() {
        val expectedUser = User(id = 1, login = "login", token = "token", avatarUrl = "avatarUrl")
        githubRepository = GithubRepositoryImpl(GithubApiMock(user = expectedUser), StarredRepositoryMock())
        val expectedResult = runBlocking {
            githubRepository.getUserInfo("token")
        }
        val actualUser = expectedResult.getOrNull()
        assertEquals(expectedUser, actualUser)
        assertTrue(expectedResult.isSuccess)
    }

    @Test
    fun getUserInfo_ApiCallGetUserInfoFails_UserWontBeSet() {
        val expectedUser = User(id = 1, login = "login", token = "token", avatarUrl = "avatarUrl")
        githubRepository = GithubRepositoryImpl(GithubApiMock(getUserInfoSuccess = false), StarredRepositoryMock())
        val expectedResult = runBlocking {
            githubRepository.getUserInfo("token")
        }
        val actualUser = expectedResult.getOrNull()
        assertNotEquals(expectedUser, actualUser)
        assertTrue(expectedResult.isFailure)
    }

    @Test
    fun getUserInfo_ApiCallGetUserStarredReposFails_UserWontBeSet() {
        val expectedUser = User(id = 1, login = "login", token = "token", avatarUrl = "avatarUrl")
        githubRepository = GithubRepositoryImpl(GithubApiMock(getUserStarredReposSuccess = false), StarredRepositoryMock())
        val expectedResult = runBlocking {
            githubRepository.getUserInfo("token")
        }
        val actualUser = expectedResult.getOrNull()
        assertNotEquals(expectedUser, actualUser)
        assertTrue(expectedResult.isFailure)
    }

    @Test
    fun getUserInfo_DbCallDeleteAllFails_UserWontBeSet() {
        val expectedUser = User(id = 1, login = "login", token = "token", avatarUrl = "avatarUrl")
        githubRepository = GithubRepositoryImpl(GithubApiMock(), StarredRepositoryMock(deleteAllThrowException = true))
        val expectedResult = runBlocking {
            githubRepository.getUserInfo("token")
        }
        val actualUser = expectedResult.getOrNull()
        assertNotEquals(expectedUser, actualUser)
        assertTrue(expectedResult.isFailure)
    }

    @Test
    fun getUserInfo_DbCallInsertAllFails_UserWontBeSet() {
        val expectedUser = User(id = 1, login = "login", token = "token", avatarUrl = "avatarUrl")
        githubRepository = GithubRepositoryImpl(GithubApiMock(), StarredRepositoryMock(insertAllThrowException = true))
        val expectedResult = runBlocking {
            githubRepository.getUserInfo("token")
        }
        val actualUser = expectedResult.getOrNull()
        assertNotEquals(expectedUser, actualUser)
        assertTrue(expectedResult.isFailure)
    }
}