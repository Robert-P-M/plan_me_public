package at.robthered.plan_me.features.data_source.domain.use_case.duplicate_task

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_description_history.CreateTaskDescriptionHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_priority_history.CreateTaskPriorityHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_title_history.CreateTaskTitleHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase
import kotlinx.datetime.Clock

class DuplicateTaskUseCaseImpl(
    private val transactionProvider: TransactionProvider,
    private val localTaskRepository: LocalTaskRepository,
    private val getTaskModelUseCase: GetTaskModelUseCase,
    private val createTaskTitleHistoryUseCase: CreateTaskTitleHistoryUseCase,
    private val createTaskDescriptionHistoryUseCase: CreateTaskDescriptionHistoryUseCase,
    private val createTaskPriorityHistoryUseCase: CreateTaskPriorityHistoryUseCase,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
    private val clock: Clock = Clock.System,
) : DuplicateTaskUseCase {
    override suspend operator fun invoke(taskId: Long): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "DuplicateTaskUseCaseImpl"
        ) {

            transactionProvider.runAsTransaction {
                val now = clock.now()
                val originalTaskModel = getTaskModelUseCase(taskId = taskId)
                    ?: return@runAsTransaction AppResult.Error(error = RoomDatabaseError.NO_TASK_FOUND)

                val newTitle = "${originalTaskModel.title} (duplicate)"


                val newTaskId = localTaskRepository
                    .insert(
                        taskModel = originalTaskModel
                            .copy(
                                taskId = 0,
                                title = newTitle,
                                createdAt = now,
                                updatedAt = now
                            )
                    )

                createTaskTitleHistoryUseCase(
                    taskId = newTaskId,
                    title = newTitle,
                    createdAt = now
                )

                if (originalTaskModel.description != null) {
                    createTaskDescriptionHistoryUseCase(
                        taskId = newTaskId,
                        description = originalTaskModel.description,
                        createdAt = now
                    )
                }

                createTaskPriorityHistoryUseCase(
                    taskId = newTaskId,
                    priorityEnum = originalTaskModel.priorityEnum
                )


                // TODO: Handle hashtags in duplication ???

                AppResult.Success(Unit)


            }
        }
    }
}