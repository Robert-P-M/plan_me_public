package at.robthered.plan_me.features.task_hashtags_dialog.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> HashtagsFlowRow(
    modifier: Modifier = Modifier,
    hashtags: List<T>,
    trailingContent: (@Composable () -> Unit) = {},
    item: @Composable FlowRowScope.(index: Int, item: T) -> Unit,
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        hashtags.forEachIndexed { index, item ->
            HashtagCardItemVisibility(
                visible = true,
                delayMillis = 50 + (index * 30)
            ) {
                item(index, item)
            }
        }
        trailingContent()
    }
}