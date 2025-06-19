package at.robthered.plan_me.features.task_hashtags_dialog.presentation.composables

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun HashtagItemActionIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    imageVector: ImageVector,
    contentDescription: String? = null,
    tintColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
) {
    Icon(
        modifier = modifier
            .clickable(onClick = onClick),
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = tintColor
    )
}