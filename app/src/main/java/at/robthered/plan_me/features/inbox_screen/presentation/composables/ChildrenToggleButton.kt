package at.robthered.plan_me.features.inbox_screen.presentation.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate

@Composable
fun ChildrenToggleButton(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    onToggle: () -> Unit,
) {
    val animateRotation by animateFloatAsState(
        targetValue = if (isExpanded) -90f else 0f,
        label = "chevron_animation"
    )
    Icon(
        modifier = modifier
            .rotate(animateRotation)
            .clickable(
                enabled = true,
                onClick = onToggle
            ),
        imageVector = Icons.Outlined.ChevronLeft,
        contentDescription = "Expand / Collapse Tasks",
        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    )
}