package at.robthered.plan_me.features.task_hashtags_dialog.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.runtime.Composable

@Composable
fun HashtagCardItemVisibility(
    visible: Boolean,
    delayMillis: Int = 0,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                delayMillis = delayMillis
            )
        ) + expandHorizontally(),
        exit = fadeOut() + shrinkHorizontally(
            animationSpec = tween(
                delayMillis = delayMillis
            )
        ),
        content = content
    )
}