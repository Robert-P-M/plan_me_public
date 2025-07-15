package at.robthered.plan_me.features.data_source.domain.use_case.add_task_schedule_event

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.model.add_task_schedule_event.AddTaskScheduleEventModel

interface AddTaskScheduleEventUseCase {
    suspend operator fun invoke(
        taskId: Long,
        addTaskScheduleEventModel: AddTaskScheduleEventModel?,
    ): AppResult<Unit, RoomDatabaseError>
}