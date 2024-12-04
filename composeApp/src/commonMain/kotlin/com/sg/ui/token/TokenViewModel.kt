package com.sg.ui.token

import androidx.lifecycle.ViewModel
import com.sg.data.repository.TokenRepository

class TokenViewModel(
    private val tokenRepository: TokenRepository = TokenRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<TokenState>(TokenState.EnterToken(tokenRepository.token))
    val state = _state.asStateFlow()

    fun handleIntent(intent: TokenIntent) {
        when (intent) {
            is TokenIntent.EnterToken -> {
            }

            is TokenIntent.ValidateToken -> {
            }
        }
    }
}