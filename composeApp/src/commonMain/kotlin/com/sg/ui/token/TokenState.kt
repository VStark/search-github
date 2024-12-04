package com.sg.ui.token

import com.sg.data.model.User


sealed class TokenState {
    data class EnterToken(
        var token: String = "",
    ) : TokenState()

    data class ValidatingToken(
        var user: User? = null,
        var validating: Boolean = false,
    ) : TokenState()
}