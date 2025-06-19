package at.robthered.plan_me.features.data_source.presentation.ext.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.Unarchive
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.sp
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.common.presentation.asString
import at.robthered.plan_me.features.common.presentation.icons.TaskDescription
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import at.robthered.plan_me.features.data_source.domain.model.task_statistics.TaskStatisticsModel
import at.robthered.plan_me.features.task_statistics_dialog.presentation.TaskStatisticsDialogUiAction
import at.robthered.plan_me.features.ui.presentation.utils.date_time.toFormattedForMenuHeader
import kotlinx.datetime.Instant

fun TaskStatisticsModel.imageVector(): ImageVector {
    return when (this) {
        is TaskStatisticsModel.TaskArchivedHistory -> {
            if (this.taskArchivedHistoryModel.isArchived)
                Icons.Outlined.Archive
            else
                Icons.Outlined.Unarchive
        }

        is TaskStatisticsModel.TaskCompletedHistory -> {
            if (this.taskCompletedHistoryModel.isCompleted)
                Icons.Outlined.CheckCircle
            else
                Icons.Outlined.Circle
        }

        is TaskStatisticsModel.TaskDescriptionHistory -> {
            TaskDescription
        }

        is TaskStatisticsModel.TaskPriorityHistory -> {
            this.taskPriorityHistoryModel.priorityEnum.imageVector()
        }

        is TaskStatisticsModel.TaskTitleHistory -> {
            Icons.Outlined.TextFields
        }

        is TaskStatisticsModel.TaskInfo -> {
            Icons.Outlined.Add
        }

        is TaskStatisticsModel.SubTask -> {
            Icons.Outlined.PostAdd
        }

        is TaskStatisticsModel.Hashtags -> {
            Icons.Outlined.Tag
        }

        is TaskStatisticsModel.ScheduleEvent ->
            Icons.Outlined.Event
    }
}

@Composable
fun TaskStatisticsModel.iconTint(): Color {
    return when (this) {
        is TaskStatisticsModel.TaskArchivedHistory -> {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        }

        is TaskStatisticsModel.TaskCompletedHistory -> {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        }

        is TaskStatisticsModel.TaskDescriptionHistory -> {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        }

        is TaskStatisticsModel.TaskPriorityHistory -> {
            this.taskPriorityHistoryModel.priorityEnum.iconTint()
        }

        is TaskStatisticsModel.TaskTitleHistory -> {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        }

        is TaskStatisticsModel.TaskInfo -> {
            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
        }

        is TaskStatisticsModel.SubTask -> {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        }

        is TaskStatisticsModel.Hashtags ->
            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)

        is TaskStatisticsModel.ScheduleEvent ->
            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
    }
}

@Composable
fun TaskStatisticsModel.titleText(): UiText {
    return when (this) {
        is TaskStatisticsModel.SubTask -> {
            UiText
                .StringResource(
                    id = R.string.task_statistics_item_title_text_sub_task,
                )
        }

        is TaskStatisticsModel.ScheduleEvent ->
            UiText
                .StringResource(
                    id = R.string.task_statistics_item_title_text_schedule_event
                )

        is TaskStatisticsModel.TaskArchivedHistory -> {
            UiText
                .StringResource(
                    id = R.string.task_statistics_item_title_text_archived_history
                )
        }

        is TaskStatisticsModel.TaskCompletedHistory -> {
            UiText
                .StringResource(
                    id = R.string.task_statistics_item_title_text_completed_history
                )
        }

        is TaskStatisticsModel.TaskDescriptionHistory -> {
            UiText
                .StringResource(
                    id = R.string.task_statistics_item_title_text_description_history
                )
        }

        is TaskStatisticsModel.TaskInfo -> {
            UiText
                .StringResource(
                    id = R.string.task_statistics_item_title_text_task_info
                )
        }

        is TaskStatisticsModel.TaskPriorityHistory -> {
            UiText
                .StringResource(
                    id = R.string.task_statistics_item_title_text_priority_history
                )
        }

        is TaskStatisticsModel.TaskTitleHistory -> {
            UiText
                .StringResource(
                    id = R.string.task_statistics_item_title_text_title_history
                )
        }

        is TaskStatisticsModel.Hashtags -> {
            UiText
                .StringResource(
                    id = R.string.task_statistics_item_title_text_hashtag_entry
                )
        }
    }
}

@Composable
fun TaskStatisticsModel.deleteText(): UiText {
    return when (this) {
        is TaskStatisticsModel.SubTask -> {
            UiText
                .StringResource(
                    id = R.string.task_statistics_item_delete_text_sub_task,
                    args = listOf("Created at ${this.createdAt.toFormattedForMenuHeader()}")
                )
        }

        is TaskStatisticsModel.ScheduleEvent ->
            UiText
                .StringResource(
                    id = R.string.task_statistics_item_delete_text_schedule_event,
                    args = listOf("Created at ${this.createdAt.toFormattedForMenuHeader()}")
                )

        is TaskStatisticsModel.TaskArchivedHistory -> {
            UiText
                .StringResource(
                    id = R.string.task_statistics_item_delete_text_archived_history,
                    args = listOf("Changed at ${this.createdAt.toFormattedForMenuHeader()}")
                )
        }

        is TaskStatisticsModel.TaskCompletedHistory -> {
            UiText
                .StringResource(
                    id = R.string.task_statistics_item_delete_text_completed_history,
                    args = listOf("Completed at ${this.createdAt.toFormattedForMenuHeader()}")
                )
        }

        is TaskStatisticsModel.TaskDescriptionHistory -> {
            UiText
                .StringResource(
                    id = R.string.task_statistics_item_delete_text_description_history,
                    args = listOf("Changed at ${this.createdAt.toFormattedForMenuHeader()}")
                )
        }

        is TaskStatisticsModel.TaskInfo -> {
            // !!! Will never happen
            UiText
                .StringResource(
                    id = R.string.task_statistics_item_delete_text_task_info,
                )
        }

        is TaskStatisticsModel.TaskPriorityHistory -> {
            val from =
                previousTaskPriorityHistoryModel?.priorityEnum.toUiText(short = true).asString()
            val to = taskPriorityHistoryModel.priorityEnum.toUiText(short = true).asString()
            val diffText = "Changed from $from to $to"
            UiText
                .StringResource(
                    id = R.string.task_statistics_item_delete_text_priority_history,
                    args = listOf(
                        diffText,
                        "Changed at ${this.createdAt.toFormattedForMenuHeader()}"
                    )
                )
        }

        is TaskStatisticsModel.TaskTitleHistory -> {
            UiText
                .StringResource(
                    id = R.string.task_statistics_item_delete_text_title_history,
                    args = listOf("Changed at ${this.createdAt.toFormattedForMenuHeader()}")
                )
        }

        is TaskStatisticsModel.Hashtags -> {
            UiText
                .StringResource(
                    id = R.string.task_statistics_item_delete_text_hashtags,
                )
        }
    }
}

@Composable
fun TaskStatisticsModel.text(): UiText {
    return when (this) {
        is TaskStatisticsModel.TaskArchivedHistory -> {
            if (this.taskArchivedHistoryModel.isArchived) {
                UiText
                    .StringResource(
                        id = R.string.task_statistics_item_archived_history_is_archived_text
                    )
            } else {
                UiText
                    .StringResource(
                        id = R.string.task_statistics_item_archived_history_is_not_archived_text
                    )
            }
        }


        is TaskStatisticsModel.TaskCompletedHistory -> {
            if (this.taskCompletedHistoryModel.isCompleted) {
                UiText
                    .StringResource(
                        id = R.string.task_statistics_item_completed_history_is_completed_text
                    )
            } else {
                UiText
                    .StringResource(
                        id = R.string.task_statistics_item_completed_history_is_not_completed_text
                    )
            }
        }

        is TaskStatisticsModel.TaskDescriptionHistory -> {
            if (this.taskDescriptionHistoryModel.text.isNullOrEmpty()) {
                UiText
                    .StringResource(
                        id = R.string.task_statistics_item_description_history_no_description
                    )
            } else {
                UiText
                    .StringResource(
                        id = R.string.task_statistics_item_description_history_description_set
                    )
            }
        }

        is TaskStatisticsModel.TaskPriorityHistory -> {
            UiText
                .StringResource(
                    id = R.string.task_statistics_item_priority_history_previous_null,
                    args = listOf(
                        taskPriorityHistoryModel.priorityEnum.toUiText(short = true).asString()
                    )
                )

        }

        is TaskStatisticsModel.TaskTitleHistory -> {
            UiText
                .StringResource(
                    id = R.string.task_statistics_item_title_history_title_set
                )
        }

        is TaskStatisticsModel.ScheduleEvent -> {
            UiText
                .StringResource(
                    id = R.string.task_statistics_item_task_schedule_set
                )
        }

        is TaskStatisticsModel.TaskInfo -> {
            UiText
                .StringResource(
                    id = R.string.task_statistics_item_task_info
                )
        }

        is TaskStatisticsModel.SubTask -> {
            UiText
                .StringResource(
                    id = R.string.task_statistics_item_sub_task_added,
                    args = listOf(this.taskModel.title)
                )
        }

        is TaskStatisticsModel.Hashtags -> {
            val hashtagNames = this.hashtags.joinToString(", ") { "#${it.name}" }
            UiText
                .PluralResource(
                    id = R.plurals.task_statistics_item_add_hashtags,
                    count = this.hashtags.size,
                    args = listOf(hashtagNames)
                )
        }
    }
}

fun TaskStatisticsModel.onClick(onAction: (action: TaskStatisticsDialogUiAction) -> Unit) {
    when (this) {
        is TaskStatisticsModel.SubTask -> {
            onAction(
                TaskStatisticsDialogUiAction
                    .OnDeleteSubTask(
                        subTaskId = this.taskModel.taskId
                    )
            )
        }

        is TaskStatisticsModel.ScheduleEvent -> {
            onAction(
                TaskStatisticsDialogUiAction
                    .OnDeleteTaskScheduleEvent(
                        taskScheduleEventId = this.taskScheduleEventModel.taskScheduleEventId
                    )
            )
        }

        is TaskStatisticsModel.TaskArchivedHistory -> {
            onAction(
                TaskStatisticsDialogUiAction
                    .OnDeleteTaskArchivedHistory(
                        taskArchivedHistoryId = this.taskArchivedHistoryModel.taskArchivedHistoryId
                    )
            )
        }

        is TaskStatisticsModel.TaskCompletedHistory -> {
            onAction(
                TaskStatisticsDialogUiAction
                    .OnDeleteTaskCompletedHistory(
                        taskCompletedHistoryId = this.taskCompletedHistoryModel.taskCompletedHistoryId
                    )
            )
        }

        is TaskStatisticsModel.TaskDescriptionHistory -> {
            onAction(
                TaskStatisticsDialogUiAction
                    .OnDeleteTaskDescriptionHistory(
                        taskDescriptionHistoryId = this.taskDescriptionHistoryModel.taskDescriptionHistoryId
                    )
            )
        }

        is TaskStatisticsModel.TaskInfo -> Unit
        is TaskStatisticsModel.TaskPriorityHistory -> {
            onAction(
                TaskStatisticsDialogUiAction
                    .OnDeleteTaskPriorityHistory(
                        taskPriorityHistoryId = this.taskPriorityHistoryModel.taskPriorityId
                    )
            )
        }

        is TaskStatisticsModel.TaskTitleHistory -> {
            onAction(
                TaskStatisticsDialogUiAction
                    .OnDeleteTaskTitleHistory(
                        taskTitleHistoryId = this.taskTitleHistoryModel.taskTitleHistoryId
                    )
            )
        }

        is TaskStatisticsModel.Hashtags -> Unit
    }
}

@Composable
fun Instant.toCreatedAtAnnotatedString(): AnnotatedString {
    return buildAnnotatedString {
        append(
            text = stringResource(R.string.created_at)
        )
        append(
            text = ": "
        )
        withLink(
            LinkAnnotation.Clickable(
                tag = this@toCreatedAtAnnotatedString.toEpochMilliseconds().toString(),
                linkInteractionListener = {},
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (0.5).sp,
                    )
                )
            )
        ) {
            append(
                text = this@toCreatedAtAnnotatedString.toFormattedForMenuHeader(),
            )
        }
    }
}

@Composable
fun List<HashtagModel>.toAnnotatedString(
    onClick: (HashtagModel) -> Unit,
): AnnotatedString {
    val listSize = this.size
    return buildAnnotatedString {
        append(
            pluralStringResource(
                id = R.plurals.task_statistics_item_add_hashtags,
                count = listSize
            )
        )
        forEachIndexed { index, hashtag ->
            if (index > 0) append(", ")
            withLink(
                LinkAnnotation.Clickable(
                    tag = hashtag.name,
                    linkInteractionListener = { tag ->
                        onClick(hashtag)
                    },
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (0.5).sp,
                            textDecoration = TextDecoration.Underline,
                        )
                    )
                )

            ) {
                append("#${hashtag.name}")
            }
        }
    }
}

@Composable
fun List<HashtagModel>.toUiTextAnnotated(onClick: (HashtagModel) -> Unit): UiText.Annotated {
    return UiText.Annotated(this.toAnnotatedString(onClick))
}