package com.sg.ui.token

sealed class TokenIntent {
    data object EnterToken : TokenIntent()
    data class ValidateToken(val token: String): TokenIntent()
}