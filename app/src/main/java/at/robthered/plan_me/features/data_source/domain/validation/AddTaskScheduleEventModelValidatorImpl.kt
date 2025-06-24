package at.robthered.plan_me.features.data_source.domain.validation

import at.robthered.plan_me.features.common.domain.getErrorOrNull
import at.robthered.plan_me.features.data_source.domain.model.add_task_schedule_event.AddTaskScheduleEventModel
import at.robthered.plan_me.features.data_source.domain.model.add_task_schedule_event.AddTaskScheduleEventModelError

class AddTaskScheduleEventModelValidatorImpl(
    private val taskScheduleStartDateEpochDaysValidator: TaskScheduleStartDateEpochDaysValidator,
) : AddTaskScheduleEventModelValidator {
    override fun invoke(addTaskScheduleEventModel: AddTaskScheduleEventModel): AddTaskScheduleEventModelError {
        return AddTaskScheduleEventModelError(
            startDateInEpochDays = taskScheduleStartDateEpochDaysValidator(value = addTaskScheduleEventModel.startDateInEpochDays).getErrorOrNull()
        )
    }
}