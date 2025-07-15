package at.robthered.plan_me.features.data_source.presentation.ext.success_message

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_schedule_event.DeleteTaskScheduleEventSuccessMessage

fun DeleteTaskScheduleEventSuccessMessage.toUiText(): UiText {
    return when (this) {
        DeleteTaskScheduleEventSuccessMessage.TASK_SCHEDULE_EVENT_DELETED_SUCCESS -> UiText
            .StringResource(
                id = R.string.delete_task_schedule_success_message
            )
    }
}