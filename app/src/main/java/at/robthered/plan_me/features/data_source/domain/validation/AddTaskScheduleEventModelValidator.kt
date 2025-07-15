package at.robthered.plan_me.features.data_source.domain.validation

import at.robthered.plan_me.features.data_source.domain.model.add_task_schedule_event.AddTaskScheduleEventModel
import at.robthered.plan_me.features.data_source.domain.model.add_task_schedule_event.AddTaskScheduleEventModelError

interface AddTaskScheduleEventModelValidator {
    operator fun invoke(addTaskScheduleEventModel: AddTaskScheduleEventModel): AddTaskScheduleEventModelError
}