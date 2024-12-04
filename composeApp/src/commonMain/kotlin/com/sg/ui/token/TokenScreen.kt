package com.sg.ui.token

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun TokenScreen(
    sendIntent: (TokenIntent) -> Unit,
    navigate: () -> Unit,
    state: TokenState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (state) {
            is TokenState.EnterToken -> EnterTokenView(
                sendIntent,
                state,
            )

            is TokenState.ValidatingToken -> ValidateTokenView(
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
    tokenState: TokenState.EnterToken,
    modifier: Modifier = Modifier
) {
    var token by remember { mutableStateOf(tokenState.token) }

    Text("Enter your token")
    TextField(
        value = token,
        onValueChange = { token = it },
        label = { Text("Token") }
    )
    Button(
        onClick = { sendIntent(TokenIntent.ValidateToken(token)) }
    ) {
        Text("Authorize")
    }
}

@Composable
fun ValidateTokenView(
    sendIntent: (TokenIntent) -> Unit,
    navigate: () -> Unit,
    tokenState: TokenState.ValidatingToken,
    modifier: Modifier = Modifier
) {
    if (tokenState.validating) {
        Text("Validating token...")
        CircularProgressIndicator()
    } else if (tokenState.user == null) {
        Text("Something went wrong")
        Button(
            onClick = { sendIntent(TokenIntent.EnterToken) }
        ) {
            Text("Try again")
        }
    } else {
        Text("Welcome ${tokenState.user?.login}!")
        AsyncImage(
            modifier = modifier.size(160.dp, 160.dp),
            model = tokenState.user?.avatarUrl,
            contentDescription = "User avatar"
        )
        Button(
            onClick = {
                navigate()
            }
        ) {
            Text("Continue")
        }
    }
}