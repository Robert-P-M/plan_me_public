package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.helper

import at.robthered.plan_me.features.data_source.domain.model.add_task_schedule_event.AddTaskScheduleEventModel

class DidAddTaskScheduleEventModelChangeCheckerImpl : DidAddTaskScheduleEventModelChangeChecker {
    override operator fun invoke(
        initialModel: AddTaskScheduleEventModel,
        currentModel: AddTaskScheduleEventModel,
    ): Boolean {
        return currentModel != initialModel
    }
}