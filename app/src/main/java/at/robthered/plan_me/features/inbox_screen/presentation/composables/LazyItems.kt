package at.robthered.plan_me.features.inbox_screen.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey

fun <T : Any> LazyListScope.lazyItems(
    lazyPagingItems: LazyPagingItems<T>,
    key: ((index: Int) -> Any)? = null,
    content: @Composable LazyItemScope.(lazyPagingItems: T?) -> Unit,
) {
    when (val refreshState = lazyPagingItems.loadState.refresh) {
        LoadState.Loading -> {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        is LoadState.Error -> {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "Error loading data.")
                    Button(onClick = { lazyPagingItems.retry() }) {
                        Text("Try again")
                    }
                }
            }
        }

        is LoadState.NotLoading -> {
            if (lazyPagingItems.itemCount == 0 && refreshState.endOfPaginationReached) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("No Items available.")
                    }
                }
            }
        }
    }
    items(
        count = lazyPagingItems.itemCount,
        key = lazyPagingItems.itemKey { it -> "${it.javaClass}-${it.hashCode()}" }
    ) { index ->
        val lazyPagingItem = lazyPagingItems[index]
        content(lazyPagingItem)
    }

    when (val appendState = lazyPagingItems.loadState.append) {
        LoadState.Loading -> {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        is LoadState.Error -> {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Error loading more. ")
                    Button(onClick = { lazyPagingItems.retry() }) {
                        Text("Try again")
                    }
                }
            }
        }

        is LoadState.NotLoading -> {}
    }
}