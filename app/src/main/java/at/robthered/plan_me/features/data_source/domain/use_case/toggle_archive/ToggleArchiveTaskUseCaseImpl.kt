package at.robthered.plan_me.features.data_source.domain.use_case.toggle_archive

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_archived.ChangeTaskArchivedHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_archived_history.CreateTaskArchivedHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase

class ToggleArchiveTaskUseCaseImpl(
    private val transactionProvider: TransactionProvider,
    private val getTaskModelUseCase: GetTaskModelUseCase,
    private val changeTaskArchivedHistoryUseCase: ChangeTaskArchivedHistoryUseCase,
    private val createTaskArchivedHistoryUseCase: CreateTaskArchivedHistoryUseCase,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
) : ToggleArchiveTaskUseCase {
    override suspend operator fun invoke(taskId: Long): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "ToggleArchiveTaskUseCaseImpl"
        ) {
            transactionProvider.runAsTransaction {

                val currentTask = getTaskModelUseCase(taskId)
                    ?: return@runAsTransaction AppResult.Error(error = RoomDatabaseError.NO_TASK_FOUND)

                val newIsArchived = !currentTask.isArchived

                val changeTaskArchivedResult = changeTaskArchivedHistoryUseCase(
                    taskId = taskId,
                    isArchived = newIsArchived
                )
                if (changeTaskArchivedResult is AppResult.Error)
                    return@runAsTransaction changeTaskArchivedResult

                val createTaskArchivedHistoryResult =
                    createTaskArchivedHistoryUseCase(
                        taskId = taskId,
                        isArchived = newIsArchived
                    )
                if (createTaskArchivedHistoryResult is AppResult.Error)
                    return@runAsTransaction createTaskArchivedHistoryResult


                AppResult.Success(Unit)
            }
        }
    }
}