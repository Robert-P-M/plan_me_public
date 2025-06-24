package at.robthered.plan_me.features.common.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

data class AppFloatingActionButtonMenuItem(
    val text: String,
    val onClick: () -> Unit,
    val imageVector: ImageVector,
    val contentDescription: String,
)