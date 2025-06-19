package at.robthered.plan_me.features.task_details_dialog.presentation.ext

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.ui.graphics.vector.ImageVector
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.model.task_tree.TaskTreeModelRootEnum

fun TaskTreeModelRootEnum.toUiText(): UiText {
    return when (this) {
        TaskTreeModelRootEnum.INBOX -> UiText
            .StringResource(
                id = R.string.task_tree_root_enum_inbox
            )
    }
}

fun TaskTreeModelRootEnum.imageVector(): ImageVector {
    return when (this) {
        TaskTreeModelRootEnum.INBOX -> Icons.Outlined.Inbox
    }
}