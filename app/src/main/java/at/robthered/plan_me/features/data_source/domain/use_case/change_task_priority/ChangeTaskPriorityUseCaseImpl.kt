package at.robthered.plan_me.features.data_source.domain.use_case.change_task_priority

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_priority_history.ChangeTaskPriorityHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_priority_history.CreateTaskPriorityHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase

class ChangeTaskPriorityUseCaseImpl(
    private val transactionProvider: TransactionProvider,
    private val getTaskModelUseCase: GetTaskModelUseCase,
    private val changeTaskPriorityHistoryUseCase: ChangeTaskPriorityHistoryUseCase,
    private val createTaskPriorityHistoryUseCase: CreateTaskPriorityHistoryUseCase,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
) : ChangeTaskPriorityUseCase {
    override suspend operator fun invoke(
        priorityEnum: PriorityEnum?,
        taskId: Long,
    ): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "ChangeTaskPriorityUseCaseImpl"
        ) {
            transactionProvider.runAsTransaction {


                val taskModel = getTaskModelUseCase(taskId = taskId)
                    ?: return@runAsTransaction AppResult.Error(error = RoomDatabaseError.NO_TASK_FOUND)

                if (taskModel.priorityEnum != priorityEnum) {
                    val changeTaskPriorityHistoryResult = changeTaskPriorityHistoryUseCase(
                        taskId = taskId,
                        priorityEnum = priorityEnum
                    )
                    if (changeTaskPriorityHistoryResult is AppResult.Error)
                        return@runAsTransaction changeTaskPriorityHistoryResult

                    val createTaskPriorityHistoryResult = createTaskPriorityHistoryUseCase(
                        taskId = taskId,
                        priorityEnum = priorityEnum
                    )
                    if (createTaskPriorityHistoryResult is AppResult.Error)
                        return@runAsTransaction createTaskPriorityHistoryResult
                }



                AppResult.Success(Unit)

            }
        }
    }
}