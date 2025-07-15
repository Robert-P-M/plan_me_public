package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.composables.picker

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PickerState<T>(initial: T) {
    var selectedItem by mutableStateOf(initial)
}

@Composable
fun <T> rememberPickerState(initial: T): PickerState<T> =
    remember { PickerState(initial) }


@Composable
fun pixelsToDp(pixels: Int) = with(LocalDensity.current) { pixels.toDp() }

fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }

@Composable
fun <T> VerticalPicker(
    modifier: Modifier = Modifier,
    items: List<T>,
    state: PickerState<T> = rememberPickerState(items.first()),
    label: @Composable (Modifier) -> Unit,
    startIndex: Int = 0,
    visibleItemsCount: Int = 5,
    dividerColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
    onChangeValue: (value: T) -> Unit = {},
    itemContent: @Composable (
        index: Int,
        item: T,
    ) -> Unit,
) {
    val visibleItemsMiddle = visibleItemsCount / 2
    val listScrollCount = Int.MAX_VALUE
    val listScrollMiddle = listScrollCount / 2
    val listStartIndex =
        listScrollMiddle - listScrollMiddle % items.size - visibleItemsMiddle + startIndex

    fun getItem(index: Int) = items[index % items.size]

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = listStartIndex)
    val flingBehaviour = rememberSnapFlingBehavior(lazyListState = listState)

    val itemHeightPixels = remember { mutableIntStateOf(0) }
    val itemHeightDp = pixelsToDp(itemHeightPixels.intValue)

    val fadingEdgeGradient = remember {
        Brush.verticalGradient(
            0f to Color.Transparent,
            0.25f to Color.Transparent,
            0.5f to Color.Black,
            0.75f to Color.Transparent,
            1f to Color.Transparent
        )
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { index -> getItem(index + visibleItemsMiddle) }
            .distinctUntilChanged()
            .collect { item ->
                state.selectedItem = item
                onChangeValue(item)
            }
    }
    Box(modifier = modifier) {
        label(
            Modifier
                .align(Alignment.TopCenter)
                .alpha(0.4f)
        )
        LazyColumn(
            state = listState,
            flingBehavior = flingBehaviour,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeightDp * visibleItemsCount)
                .fadingEdge(fadingEdgeGradient)
        ) {
            items(count = listScrollCount) { index ->
                val item = getItem(index)
                Box(
                    modifier = Modifier.onSizeChanged {
                        itemHeightPixels.intValue = it.height
                    }
                ) {
                    itemContent(index, item)
                }
            }
        }

        HorizontalDivider(
            color = dividerColor,
            modifier = Modifier.offset(y = itemHeightDp * visibleItemsMiddle)
        )
        HorizontalDivider(
            color = dividerColor,
            modifier = Modifier.offset(y = itemHeightDp * (visibleItemsMiddle + 1))
        )
    }
}