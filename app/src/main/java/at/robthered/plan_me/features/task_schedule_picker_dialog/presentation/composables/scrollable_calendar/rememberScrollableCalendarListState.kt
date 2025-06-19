package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.composables.scrollable_calendar

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

class ScrollableCalendarListState(
    val lazyListState: LazyListState,
    val flingBehavior: FlingBehavior,
    val setHeaderHeight: (Int) -> Unit,
)

@Composable
fun rememberScrollableCalendarListState(): ScrollableCalendarListState {

    val lazyListState = rememberLazyListState()
    val density = LocalDensity.current
    var headerHeight by remember { mutableIntStateOf(0) }
    val snapOffsetPx by remember(headerHeight) {
        derivedStateOf {
            with(density) {
                headerHeight.div(5).times(2).minus(10).dp.roundToPx()
            }
        }
    }
    val snapLayoutInfoProvider = remember(lazyListState, density, snapOffsetPx) {
        val snapPosition = object : SnapPosition {
            override fun position(
                layoutSize: Int,
                itemSize: Int,
                beforeContentPadding: Int,
                afterContentPadding: Int,
                itemIndex: Int,
                itemCount: Int,
            ): Int {
                return with(density) { beforeContentPadding + snapOffsetPx }
            }
        }
        SnapLayoutInfoProvider(lazyListState, snapPosition)
    }


    val flingBehavior = rememberSnapFlingBehavior(snapLayoutInfoProvider)

    val setHeaderHeight: (Int) -> Unit = remember(density) {
        { newHeight ->
            headerHeight = newHeight
        }
    }

    return ScrollableCalendarListState(
        lazyListState = lazyListState,
        flingBehavior = flingBehavior,
        setHeaderHeight = setHeaderHeight
    )

}