package at.robthered.plan_me.features.data_source.domain.use_case.update_task

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.model.update_task.UpdateTaskModel
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_description_history.CreateTaskDescriptionHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_priority_history.CreateTaskPriorityHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_title_history.CreateTaskTitleHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.update_task_description.UpdateTaskDescriptionUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.update_task_priority.UpdateTaskPriorityUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.update_task_title.UpdateTaskTitleUseCase
import kotlinx.datetime.Clock


class UpdateTaskUseCaseImpl(
    private val transactionProvider: TransactionProvider,
    private val getTaskModelUseCase: GetTaskModelUseCase,
    private val createTaskTitleHistoryUseCase: CreateTaskTitleHistoryUseCase,
    private val updateTaskTitleUseCase: UpdateTaskTitleUseCase,
    private val createTaskDescriptionHistoryUseCase: CreateTaskDescriptionHistoryUseCase,
    private val updateTaskDescriptionUseCase: UpdateTaskDescriptionUseCase,
    private val createTaskPriorityHistoryUseCase: CreateTaskPriorityHistoryUseCase,
    private val updateTaskPriorityUseCase: UpdateTaskPriorityUseCase,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
    private val clock: Clock = Clock.System,
) : UpdateTaskUseCase {
    override suspend operator fun invoke(updateTaskModel: UpdateTaskModel): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "UpdateTaskUseCaseImpl"
        ) {
            transactionProvider.runAsTransaction {
                val createdAt = clock.now()

                val currentTaskModel = getTaskModelUseCase(taskId = updateTaskModel.taskId)
                    ?: return@runAsTransaction AppResult.Error(error = RoomDatabaseError.NO_TASK_FOUND)


                if (currentTaskModel.title != updateTaskModel.title) {
                    val createTaskTitleHistoryResult = createTaskTitleHistoryUseCase(
                        taskId = updateTaskModel.taskId,
                        title = updateTaskModel.title,
                        createdAt = createdAt
                    )
                    if (createTaskTitleHistoryResult is AppResult.Error) {
                        return@runAsTransaction createTaskTitleHistoryResult
                    }
                    val updateTaskTitleResult = updateTaskTitleUseCase(
                        taskId = updateTaskModel.taskId,
                        title = updateTaskModel.title,
                        createdAt = createdAt
                    )
                    if (updateTaskTitleResult is AppResult.Error)
                        return@runAsTransaction updateTaskTitleResult

                }

                if (currentTaskModel.description != updateTaskModel.description) {
                    val createTaskDescriptionHistoryResult = createTaskDescriptionHistoryUseCase(
                        taskId = updateTaskModel.taskId,
                        description = updateTaskModel.description,
                        createdAt = createdAt
                    )
                    if (createTaskDescriptionHistoryResult is AppResult.Error)
                        return@runAsTransaction createTaskDescriptionHistoryResult
                    val updateTaskDescriptionResult = updateTaskDescriptionUseCase(
                        taskId = updateTaskModel.taskId,
                        description = updateTaskModel.description,
                        createdAt = createdAt
                    )
                    if (updateTaskDescriptionResult is AppResult.Error)
                        return@runAsTransaction updateTaskDescriptionResult

                }

                if (currentTaskModel.priorityEnum != updateTaskModel.priorityEnum) {
                    val updateTaskPriorityResult = updateTaskPriorityUseCase(
                        taskId = updateTaskModel.taskId,
                        priorityEnum = updateTaskModel.priorityEnum,
                        createdAt = createdAt
                    )
                    if (updateTaskPriorityResult is AppResult.Error)
                        return@runAsTransaction updateTaskPriorityResult
                    val createTaskPriorityHistoryResult = createTaskPriorityHistoryUseCase(
                        taskId = updateTaskModel.taskId,
                        priorityEnum = updateTaskModel.priorityEnum,
                    )
                    if (createTaskPriorityHistoryResult is AppResult.Error)
                        return@runAsTransaction createTaskPriorityHistoryResult
                }


                AppResult.Success(Unit)
            }
        }
    }
}