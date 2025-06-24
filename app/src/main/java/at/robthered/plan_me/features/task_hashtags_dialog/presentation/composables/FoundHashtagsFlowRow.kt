package at.robthered.plan_me.features.task_hashtags_dialog.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel

@Composable
fun FoundHashtagsFlowRow(
    modifier: Modifier = Modifier,
    foundHashtags: List<UiHashtagModel.FoundHashtagModel>,
    onSaveExistingHashtag: (existingHashtagModel: UiHashtagModel.ExistingHashtagModel) -> Unit,
    onResetState: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAnimation by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )
    AnimatedVisibility(
        foundHashtags.isNotEmpty(),
        enter = fadeIn(
            animationSpec = tween(
                delayMillis = 100
            )
        ) + expandVertically(),
        exit = fadeOut()
                + shrinkVertically(
            animationSpec = tween(
                delayMillis = 100
            )
        )
    ) {
        val totalDelay = 50 + (foundHashtags.size * 30)
        FlowRow(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {

            foundHashtags.forEachIndexed { index, foundHashtag: UiHashtagModel.FoundHashtagModel ->
                if (index == 0) {
                    HashtagCardItemVisibility(
                        visible = true,
                        delayMillis = 50,
                    ) {
                        HashtagItemActionIcon(
                            onClick = onResetState,
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                        )
                    }
                }
                HashtagCardItemVisibility(
                    visible = true,
                    delayMillis = 50 + (index * 30),
                ) {
                    HashtagCardItem(
                        text = foundHashtag.name,
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(
                            alpha = pulseAnimation
                        ),
                        borderColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                            alpha = pulseAnimation - 0.3f
                        )
                    ) {
                        HashtagItemActionIcon(
                            modifier = Modifier,
                            onClick = {
                                onSaveExistingHashtag(
                                    UiHashtagModel.ExistingHashtagModel(
                                        hashtagId = foundHashtag.hashtagId,
                                        name = foundHashtag.name
                                    )
                                )
                            },
                            imageVector = Icons.Outlined.AddCircleOutline,
                            contentDescription = "Add hashtag",
                            tintColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                alpha = 0.8f
                            )
                        )
                    }
                }
                if (index == foundHashtags.lastIndex) {
                    HashtagCardItemVisibility(
                        visible = true,
                        delayMillis = totalDelay,
                    ) {
                        HashtagItemActionIcon(
                            onClick = onResetState,
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null,
                        )
                    }
                }
            }

        }
    }
}