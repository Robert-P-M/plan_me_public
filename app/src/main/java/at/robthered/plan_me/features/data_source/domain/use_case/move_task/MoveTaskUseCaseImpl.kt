package at.robthered.plan_me.features.data_source.domain.use_case.move_task

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.model.move_task.MoveTaskUseCaseArgs
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase
import kotlinx.datetime.Clock

class MoveTaskUseCaseImpl(
    private val localTaskRepository: LocalTaskRepository,
    private val transactionProvider: TransactionProvider,
    private val getTaskModelUseCase: GetTaskModelUseCase,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
    private val clock: Clock = Clock.System,
) : MoveTaskUseCase {
    override suspend operator fun invoke(moveTaskUseCaseArgs: MoveTaskUseCaseArgs): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "MoveTaskUseCaseImpl"
        ) {
            transactionProvider.runAsTransaction {
                val updatedAt = clock.now()


                val currentTaskModel =
                    getTaskModelUseCase(taskId = moveTaskUseCaseArgs.taskId)
                        ?: return@runAsTransaction AppResult.Error(error = RoomDatabaseError.NO_TASK_FOUND)

                val taskToUpdate = currentTaskModel
                    .copy(
                        sectionId = moveTaskUseCaseArgs.sectionId,
                        parentTaskId = moveTaskUseCaseArgs.parentTaskId,
                        updatedAt = updatedAt
                    )

                localTaskRepository.upsert(taskModel = taskToUpdate)

                AppResult.Success(Unit)
            }
        }
    }
}