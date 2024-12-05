package com.sg.ui.token

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.sg.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TokenViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val logger = Logger.withTag("TokenViewModel")
    private val _state = MutableStateFlow<TokenState>(TokenState.EnterToken)
    val state = _state.asStateFlow()

    fun handleIntent(intent: TokenIntent) {
        when (intent) {
            is TokenIntent.EnterToken -> {
                _state.update { TokenState.EnterToken }
            }

            is TokenIntent.ValidateToken -> {
                _state.update { TokenState.ValidatingToken }
                viewModelScope.launch {
                    userRepository
                        .initUser(intent.token)
                        .fold(
                            onSuccess = { user ->
                                logger.i { "Token validation successful" }
                                _state.update {
                                    TokenState.TokenValidated(user)
                                }
                            },
                            onFailure = { error ->
                                logger.e(error) { "Token validation failed" }
                                _state.update {
                                    TokenState.TokenValidationFailed("Token validation failed")
                                }
                            }
                        )
                }
            }
        }
    }
}