package at.robthered.plan_me.features.data_source.domain.use_case.delete_task_schedule_event

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskScheduleEventRepository

class DeleteTaskScheduleEventUseCaseImpl(
    private val localTaskScheduleEventRepository: LocalTaskScheduleEventRepository,
    private val transactionProvider: TransactionProvider,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
) : DeleteTaskScheduleEventUseCase {
    override suspend operator fun invoke(taskScheduleEventId: Long): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "DeleteTaskScheduleEventUseCaseImpl"
        ) {
            transactionProvider
                .runAsTransaction {
                    val rowsAffected =
                        localTaskScheduleEventRepository
                            .delete(taskScheduleEventId = taskScheduleEventId)
                    if (rowsAffected == 0)
                        return@runAsTransaction AppResult.Error(error = RoomDatabaseError.NO_TASK_SCHEDULE_EVENT_FOUND)
                    AppResult.Success(Unit)
                }
        }
    }
}