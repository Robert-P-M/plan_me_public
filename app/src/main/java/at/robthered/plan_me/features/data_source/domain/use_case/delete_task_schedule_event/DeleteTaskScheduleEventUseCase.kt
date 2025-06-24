package at.robthered.plan_me.features.data_source.domain.use_case.delete_task_schedule_event

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError

interface DeleteTaskScheduleEventUseCase {
    suspend operator fun invoke(taskScheduleEventId: Long): AppResult<Unit, RoomDatabaseError>
}