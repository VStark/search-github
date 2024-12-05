package com.sg.data.repository

import com.sg.data.model.User
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

class UserRepositoryTest {

    @Test
    fun getToken_SetTokenAndReturnSuccess_GetExpectedToken() {
        val userRepository: UserRepository = UserRepositoryImpl(GithubRepositoryMock(User()), StarredRepositoryMock())
        val expectedToken = "newToken"
        val actualSuccess = runBlocking {
            userRepository.initUser(expectedToken)
        }.isSuccess

        val actualToken = userRepository.getUser().token
        assertEquals(expectedToken, actualToken)
        assertTrue(actualSuccess)
    }

    @Test
    fun getToken_SetTokenAndReturnFailure_TokenCannotBeRetrievedUserIsNotSet() {
        val userRepository: UserRepository = UserRepositoryImpl(GithubRepositoryMock(User(), getUserInfoSuccess = false), StarredRepositoryMock())
        val expectedToken = "newToken"
        val actualFailure = runBlocking {
            userRepository.initUser(expectedToken)
        }.isFailure

        var actualToken = ""
        try {
            actualToken = userRepository.getUser().token
        } catch (_: Exception) { }
        assertNotEquals(expectedToken, actualToken)
        assertTrue(actualFailure)
    }

    @Test
    fun getUser_InitAndGetUserAndReturnSuccess_GetExpectedUser() {
        val expectedToken = "newToken"
        val expectedUser = User(id = 1, login = "login", token = expectedToken, avatarUrl = "avatarUrl")
        val userRepository: UserRepository = UserRepositoryImpl(GithubRepositoryMock(expectedUser), StarredRepositoryMock())
        val actualSuccess = runBlocking {
            userRepository.initUser(expectedToken)
        }.isSuccess

        val actualUser = userRepository.getUser()
        assertEquals(expectedUser, actualUser)
        assertTrue(actualSuccess)
    }

    @Test
    fun getUser_InitUserAndReturnFailure_UserWontBeSet() {
        val expectedToken = "newToken"
        val expectedUser = User(id = 1, login = "login", token = expectedToken, avatarUrl = "avatarUrl")
        val userRepository: UserRepository = UserRepositoryImpl(GithubRepositoryMock(expectedUser, getUserInfoSuccess = false), StarredRepositoryMock())
        val actualFailure = runBlocking {
            userRepository.initUser(expectedToken)
        }.isFailure

        try {
            userRepository.getUser()
        } catch (e: Exception) {
            assertIs<UninitializedPropertyAccessException>(e)
        }
        assertTrue(actualFailure)
    }

    @Test
    fun initUser_SetUserAndTokenAndReturnSuccess_GetExpectedUser() {
        val expectedToken = "newToken"
        val expectedUser = User(id = 1, login = "login", token = expectedToken, avatarUrl = "avatarUrl")
        val userRepository: UserRepository = UserRepositoryImpl(GithubRepositoryMock(expectedUser), StarredRepositoryMock())
        val actualSuccess = runBlocking {
            userRepository.initUser(expectedToken)
        }.isSuccess

        val actualUser = userRepository.getUser()
        assertEquals(expectedUser, actualUser)
        assertTrue(actualSuccess)
    }

    @Test
    fun initUser_SetUserAndTokenAndReturnFailure_UserWontBeSet() {
        val expectedToken = "newToken"
        val expectedUser = User(id = 1, login = "login", token = expectedToken, avatarUrl = "avatarUrl")
        val userRepository: UserRepository = UserRepositoryImpl(GithubRepositoryMock(expectedUser), StarredRepositoryMock())
        val actualSuccess = runBlocking {
            userRepository.initUser(expectedToken)
        }.isSuccess

        val actualUser = userRepository.getUser()
        assertEquals(expectedUser, actualUser)
        assertTrue(actualSuccess)
    }
}