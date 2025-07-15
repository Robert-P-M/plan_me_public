package at.robthered.plan_me.features.data_source.domain.use_case.add_task

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.data.local.mapper.toTaskModel
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.model.add_task.AddTaskModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.use_case.add_task_schedule_event.AddTaskScheduleEventUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_description_history.CreateTaskDescriptionHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_hashtag_cross_ref.CreateTaskHashtagCrossRefUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_priority_history.CreateTaskPriorityHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_title_history.CreateTaskTitleHistoryUseCase
import kotlinx.datetime.Clock

class AddTaskUseCaseImpl(
    private val transactionProvider: TransactionProvider,
    private val localTaskRepository: LocalTaskRepository,
    private val createTaskHashtagCrossRefUseCase: CreateTaskHashtagCrossRefUseCase,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
    private val clock: Clock = Clock.System,
    private val createTaskTitleHistoryUseCase: CreateTaskTitleHistoryUseCase,
    private val createTaskDescriptionHistoryUseCase: CreateTaskDescriptionHistoryUseCase,
    private val createTaskPriorityHistoryUseCase: CreateTaskPriorityHistoryUseCase,
    private val addTaskScheduleEventUseCase: AddTaskScheduleEventUseCase,
) : AddTaskUseCase {
    override suspend operator fun invoke(addTaskModel: AddTaskModel): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "AddTaskUseCaseImpl"
        ) {
            transactionProvider.runAsTransaction {
                val now = clock.now()

                val taskModel = addTaskModel.toTaskModel(now = now)

                val newTaskId = localTaskRepository.insert(
                    taskModel = taskModel
                )
                val createTaskTitleHistoryResult = createTaskTitleHistoryUseCase(
                    taskId = newTaskId,
                    title = addTaskModel.title,
                    createdAt = now
                )
                if (createTaskTitleHistoryResult is AppResult.Error)
                    return@runAsTransaction createTaskTitleHistoryResult

                val createTaskDescriptionHistoryResult = createTaskDescriptionHistoryUseCase(
                    taskId = newTaskId,
                    description = addTaskModel.description,
                    createdAt = now
                )
                if (createTaskDescriptionHistoryResult is AppResult.Error)
                    return@runAsTransaction createTaskDescriptionHistoryResult

                val createTaskPriorityHistoryResult = createTaskPriorityHistoryUseCase(
                    taskId = newTaskId,
                    priorityEnum = addTaskModel.priorityEnum,
                )
                if (createTaskPriorityHistoryResult is AppResult.Error) {
                    return@runAsTransaction createTaskPriorityHistoryResult
                }

                val createTaskHashtagCrossRefResult = createTaskHashtagCrossRefUseCase(
                    taskId = newTaskId,
                    hashtags = addTaskModel.hashtags
                )
                if (createTaskHashtagCrossRefResult is AppResult.Error)
                    return@runAsTransaction createTaskHashtagCrossRefResult
                val scheduleResult = addTaskScheduleEventUseCase(
                    taskId = newTaskId,
                    addTaskScheduleEventModel = addTaskModel.taskSchedule
                )
                if (scheduleResult is AppResult.Error) {
                    return@runAsTransaction scheduleResult
                }

                AppResult.Success(Unit)


            }
        }
    }
}