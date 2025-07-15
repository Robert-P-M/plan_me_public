package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.helper

import at.robthered.plan_me.features.data_source.domain.model.add_task_schedule_event.AddTaskScheduleEventModel

interface DidAddTaskScheduleEventModelChangeChecker {
    operator fun invoke(
        initialModel: AddTaskScheduleEventModel,
        currentModel: AddTaskScheduleEventModel,
    ): Boolean
}