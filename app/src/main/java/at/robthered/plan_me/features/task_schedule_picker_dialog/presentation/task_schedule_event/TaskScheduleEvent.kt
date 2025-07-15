package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.task_schedule_event

import at.robthered.plan_me.features.data_source.domain.local.models.TaskScheduleEventModel
import at.robthered.plan_me.features.data_source.domain.model.add_task_schedule_event.AddTaskScheduleEventModel

sealed class TaskScheduleEvent {
    data class CurrentAddTaskSchedule(val addTaskScheduleEventModel: AddTaskScheduleEventModel?) :
        TaskScheduleEvent()

    data class NewAddTaskSchedule(val addTaskScheduleEventModel: AddTaskScheduleEventModel?) :
        TaskScheduleEvent()

    data class CurrentTaskScheduleEventModel(val taskScheduleEventModel: TaskScheduleEventModel?) :
        TaskScheduleEvent()

    data object ClearEvent : TaskScheduleEvent()
}