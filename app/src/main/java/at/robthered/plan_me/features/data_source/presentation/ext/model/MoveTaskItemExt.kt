package at.robthered.plan_me.features.data_source.presentation.ext.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.common.presentation.asString
import at.robthered.plan_me.features.common.presentation.icons.SectionIcon
import at.robthered.plan_me.features.common.presentation.navigation.MoveTaskDialogArgs
import at.robthered.plan_me.features.data_source.domain.model.move_task.MoveTaskModel
import at.robthered.plan_me.features.data_source.domain.model.move_task.MoveTaskModelRootEnum
import at.robthered.plan_me.features.data_source.domain.model.move_task.MoveTaskUseCaseArgs

fun MoveTaskModelRootEnum.toUiText(): UiText {
    return when (this) {
        MoveTaskModelRootEnum.INBOX -> UiText
            .StringResource(
                id = R.string.move_task_item_root_inbox
            )
    }
}

fun MoveTaskModelRootEnum.imageVector(): ImageVector {
    return when (this) {
        MoveTaskModelRootEnum.INBOX ->
            Icons.Outlined.Inbox

    }
}

fun MoveTaskModel.imageVector(): ImageVector {
    return when (this) {
        is MoveTaskModel.Root -> {
            this.moveTaskModelRootEnum.imageVector()
        }

        is MoveTaskModel.Section -> SectionIcon
        is MoveTaskModel.Task -> Icons.Outlined.Checklist
    }
}

@Composable
fun MoveTaskModel.title(): String {
    return when (this) {
        is MoveTaskModel.Root -> {
            this.moveTaskModelRootEnum.toUiText().asString()
        }

        is MoveTaskModel.Section -> this.title
        is MoveTaskModel.Task -> this.title
    }
}

fun MoveTaskModel.isCurrent(moveTaskDialogArgs: MoveTaskDialogArgs): Boolean {
    return when (this) {
        is MoveTaskModel.Root -> {
            moveTaskDialogArgs.parentTaskId == null && moveTaskDialogArgs.sectionId == null
        }

        is MoveTaskModel.Section -> {
            moveTaskDialogArgs.sectionId == this.sectionId
        }

        is MoveTaskModel.Task -> {
            moveTaskDialogArgs.parentTaskId == this.taskId
        }
    }
}

fun MoveTaskModel.isOwn(moveTaskDialogArgs: MoveTaskDialogArgs): Boolean {
    return when (this) {
        is MoveTaskModel.Root -> {
            moveTaskDialogArgs.parentTaskId == null && moveTaskDialogArgs.sectionId == null
        }

        is MoveTaskModel.Section -> {
            moveTaskDialogArgs.sectionId == this.sectionId
        }

        is MoveTaskModel.Task -> {
            moveTaskDialogArgs.taskId == this.taskId
        }
    }
}


fun MoveTaskModel.toMoveTaskUseCaseArgs(taskId: Long): MoveTaskUseCaseArgs {
    return when (this) {
        is MoveTaskModel.Root -> MoveTaskUseCaseArgs(
            taskId = taskId,
            sectionId = null,
            parentTaskId = null,
        )

        is MoveTaskModel.Section -> MoveTaskUseCaseArgs(
            taskId = taskId,
            sectionId = sectionId,
            parentTaskId = null,
        )

        is MoveTaskModel.Task -> MoveTaskUseCaseArgs(
            taskId = taskId,
            sectionId = null,
            parentTaskId = this.taskId,
        )
    }
}