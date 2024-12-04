package com.sg.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import co.touchlab.kermit.Logger
import com.sg.data.model.Repo
import kotlin.math.ceil

@Composable
fun SearchScreen(
    searchQueryState: String,
    state: SearchState,
    sendIntent: (SearchIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    MaterialTheme {
        Column(
            modifier = modifier.padding(16.dp),
        ) {
            SearchQueryView(
                state = searchQueryState,
                sendIntent = sendIntent,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.size(16.dp))
            ResultListView(
                state = state,
                sendIntent = sendIntent,
                modifier = modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}

@Composable
fun SearchQueryView(
    state: String,
    sendIntent: (SearchIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = state,
        onValueChange = {
            sendIntent(SearchIntent.SearchRepository(it))
        },
        label = { Text("Enter Repository name") },
        placeholder = { Text("Enter at least 2 characters") },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            sendIntent(SearchIntent.SearchRepository(state))
        }),
    )
}

@Composable
fun ResultListView(
    state: SearchState,
    sendIntent: (SearchIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        is SearchState.Init -> {
            return
        }

        is SearchState.Loading -> {
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
            }
        }

        is SearchState.Success -> {
            LazyColumn(
                modifier = modifier
                    .sizeIn(minHeight = 200.dp, maxHeight = 400.dp)
                    .fillMaxWidth()
                    .shadow(1.dp)
                    .background(MaterialTheme.colorScheme.surface),
                userScrollEnabled = false,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
            }

            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                    ) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, "Previous")
                    }
                    IconButton(
                    ) {
                        Icon(Icons.AutoMirrored.Default.ArrowForward, "Next")
                    }
                }
            }
        }

        is SearchState.Error -> {
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(state.error)
            }
        }
    }
}

@Composable
fun ItemView(
    sendIntent: (SearchIntent) -> Unit,
    repo: Repo,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val colors = MaterialTheme.colorScheme
    val itemColor =
        if (isHovered) colors.secondaryContainer else MaterialTheme.colorScheme.surface

    Column {
        ListItem(
            modifier = modifier.hoverable(interactionSource = interactionSource),
            headlineContent = { Text(repo.name) },
            supportingContent = { Text(repo.owner) },
            trailingContent = {
                IconButton(
                    onClick = {
                        sendIntent(SearchIntent.ToggleStar(repo.nodeId, !repo.hasStar))
                    },
                ) {
                    Icon(
                        if (repo.hasStar) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                        "Favorite",
                        modifier = Modifier.size(24.dp),
                    )
                }
            },
            colors = ListItemDefaults.colors(containerColor = itemColor),
        )
        HorizontalDivider()
    }
}