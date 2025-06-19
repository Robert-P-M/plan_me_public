package at.robthered.plan_me.features.data_source.domain.use_case.toggle_complete

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_completed.ChangeTaskCompletedUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_completed_history.CreateTaskCompletedHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase

class ToggleCompleteTaskUseCaseImpl(
    private val transactionProvider: TransactionProvider,
    private val getTaskModelUseCase: GetTaskModelUseCase,
    private val createTaskCompletedHistoryUseCase: CreateTaskCompletedHistoryUseCase,
    private val changeTaskCompletedUseCase: ChangeTaskCompletedUseCase,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
) : ToggleCompleteTaskUseCase {
    override suspend fun invoke(taskId: Long): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "ToggleCompleteTaskUseCaseImpl"
        ) {
            transactionProvider.runAsTransaction {
                val currentTask = getTaskModelUseCase(taskId)
                    ?: return@runAsTransaction AppResult.Error(error = RoomDatabaseError.NO_TASK_FOUND)

                val newIsCompleted = !currentTask.isCompleted

                val changeTaskCompletedResult = changeTaskCompletedUseCase(
                    taskId = taskId,
                    isCompleted = newIsCompleted
                )
                if (changeTaskCompletedResult is AppResult.Error)
                    return@runAsTransaction changeTaskCompletedResult


                val createTaskCompletedHistoryResult = createTaskCompletedHistoryUseCase(
                    taskId = taskId,
                    isCompleted = newIsCompleted
                )
                if (createTaskCompletedHistoryResult is AppResult.Error)
                    return@runAsTransaction createTaskCompletedHistoryResult

                AppResult.Success(Unit)

            }

        }
    }

}