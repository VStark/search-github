package com.sg.ui.token

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@Composable
fun TokenScreen(
    sendIntent: (TokenIntent) -> Unit,
    navigate: () -> Unit,
    state: TokenState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (state) {
            is TokenState.EnterToken -> EnterTokenView(
                sendIntent,
                modifier,
            )

            is TokenState.ValidatingToken,
            is TokenState.TokenValidated,
            is TokenState.TokenValidationFailed -> ValidateTokenView(
                sendIntent,
                navigate,
                state,
            )
        }
    }
}

@Composable
fun EnterTokenView(
    sendIntent: (TokenIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var token by remember { mutableStateOf("") }
    var enabled by remember { mutableStateOf(false) }

    Text("Enter your Github token")
    TextField(
        value = token,
        onValueChange = {
            enabled = it.isNotEmpty()
            token = it
        },
        label = { Text("Token") },
        modifier = modifier.width(280.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            if (enabled) {
                sendIntent(TokenIntent.ValidateToken(token))
            }
        }),
    )
    Button(
        onClick = {
            sendIntent(TokenIntent.ValidateToken(token))
        },
        enabled = enabled,
    ) {
        Text("Authorize")
    }
}

@Composable
fun ValidateTokenView(
    sendIntent: (TokenIntent) -> Unit,
    navigate: () -> Unit,
    state: TokenState,
    modifier: Modifier = Modifier,
) {
    when(state) {
        is TokenState.ValidatingToken -> {
            Text("Validating token...")
            CircularProgressIndicator()
        }
        is TokenState.TokenValidated -> {
            Text(
                "Welcome ${state.user.login}!",
                fontSize = 18.sp,
            )
            AsyncImage(
                modifier = modifier.size(80.dp, 80.dp),
                model = state.user.avatarUrl,
                contentDescription = "User avatar",
            )
            Button(
                onClick = {
                    navigate()
                },
            ) {
                Text("Continue")
            }
        }
        is TokenState.TokenValidationFailed -> {
            Text("Something went wrong")
            Button(
                onClick = {
                    sendIntent(TokenIntent.EnterToken)
                },
            ) {
                Text("Try again")
            }
        }
        else -> {}
    }
}