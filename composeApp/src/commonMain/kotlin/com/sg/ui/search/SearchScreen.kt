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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
                modifier = modifier.fillMaxWidth(),
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
        label = { Text("Repository Name") },
        placeholder = { Text("Enter at least 3 characters") },
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
            InfoBox(
                "Enter repository name to search.",
                modifier,
            )
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("")
            }
        }

        is SearchState.Loading -> {
            LoadingBox(modifier)
        }

        is SearchState.Success -> {
            val pagingState = state.paging.collectAsLazyPagingItems()
            var pageIndex by remember { mutableIntStateOf(1) }
            var loading by remember { mutableStateOf(false) }
            val pageSize = 5
            val lastPage = pageIndex == ceil(
                pagingState.itemCount.toDouble() / pageSize.toDouble()
            ).toInt()

            if (pagingState.itemCount == 0) {
                InfoBox(
                    "No repositories found.",
                    modifier,
                )
                return
            }

            if (loading) {
                FlatLoadingBox(modifier)
            }

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
                val remainItems = pagingState.itemCount % pageSize
                val itemsCount =
                    if (pagingState.itemCount == 0) {
                        pagingState.itemCount
                    } else if (remainItems == 0 || !lastPage) {
                        pageSize
                    } else {
                        remainItems
                    }

                items(
                    itemsCount,
                ) { index ->
                    var newItemIndex = index
                    if (pageIndex > 1) {
                        newItemIndex = (pageIndex - 1) * pageSize + index
                    }
                    val item = pagingState[newItemIndex]
                    if (item != null) {
                        ItemView(sendIntent, item)
                    }
                }

                pagingState.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            pageIndex = 1
                            loading = true
                        }
                        loadState.append is LoadState.Loading ||
                        loadState.prepend is LoadState.Loading -> {
                            loading = true
                        }
                        loadState.refresh is LoadState.NotLoading ||
                        loadState.append is LoadState.NotLoading ||
                        loadState.prepend is LoadState.NotLoading -> {
                            loading = false
                        }
                    }
                }
            }

            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = {
                            pageIndex -= 1
                        },
                        enabled = pageIndex > 1,
                    ) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, "Previous")
                    }
                    IconButton(
                        onClick = {
                            pageIndex += 1
                        },
                        enabled = pageIndex < ceil(
                            pagingState.itemCount.toDouble() / pageSize.toDouble()
                        ).toInt()
                    ) {
                        Icon(Icons.AutoMirrored.Default.ArrowForward, "Next")
                    }
                }
            }
        }

        is SearchState.Error -> {
            InfoBox(
                "Something went wrong. \n\n ${state.error}",
                modifier,
            )
        }
    }
}

@Composable
fun InfoBox(
    text: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text)
    }
}

@Composable
fun LoadingBox(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun FlatLoadingBox(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
        )
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
                        sendIntent(SearchIntent.ToggleStar(repo.id, repo.nodeId, !repo.hasStar))
                    },
                ) {
                    Icon(
                        if (repo.hasStar) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                        "Starred",
                        modifier = Modifier.size(24.dp),
                    )
                }
            },
            colors = ListItemDefaults.colors(containerColor = itemColor),
        )
        HorizontalDivider()
    }
}