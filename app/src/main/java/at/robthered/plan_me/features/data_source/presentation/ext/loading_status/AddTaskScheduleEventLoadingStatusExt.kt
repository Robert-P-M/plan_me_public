package at.robthered.plan_me.features.data_source.presentation.ext.loading_status

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.add_task_schedule_event.AddTaskScheduleEventLoadingStatus

fun AddTaskScheduleEventLoadingStatus.toUiText(): UiText {
    return when (this) {
        AddTaskScheduleEventLoadingStatus.STARTING -> {
            UiText
                .StringResource(
                    id = R.string.add_task_schedule_event_loading_status_starting
                )
        }
    }
}