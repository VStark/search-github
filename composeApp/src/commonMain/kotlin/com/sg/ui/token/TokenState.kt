package com.sg.ui.token

import com.sg.data.model.User


sealed class TokenState {
    data object EnterToken : TokenState()
    data object ValidatingToken : TokenState()
    data class TokenValidated(val user: User) : TokenState()
    data class TokenValidationFailed(val error: String) : TokenState()
}