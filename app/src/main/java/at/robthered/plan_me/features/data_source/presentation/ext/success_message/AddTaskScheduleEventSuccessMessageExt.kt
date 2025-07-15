package at.robthered.plan_me.features.data_source.presentation.ext.success_message

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.add_task_schedule_event.AddTaskScheduleEventSuccessMessage

fun AddTaskScheduleEventSuccessMessage.toUiText(): UiText {
    return when (this) {
        AddTaskScheduleEventSuccessMessage.TASK_EVENT_CHANGED -> UiText
            .StringResource(
                id = R.string.add_task_schedule_event_success_message_task_event_changed
            )
    }
}