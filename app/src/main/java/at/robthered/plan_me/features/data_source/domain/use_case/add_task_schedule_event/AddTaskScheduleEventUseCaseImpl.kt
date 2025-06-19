package at.robthered.plan_me.features.data_source.domain.use_case.add_task_schedule_event

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.common.domain.notification.AppAlarmScheduler
import at.robthered.plan_me.features.common.domain.use_case.NotificationContentMapper
import at.robthered.plan_me.features.data_source.data.local.mapper.toTaskScheduleEventModel
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.model.add_task_schedule_event.AddTaskScheduleEventModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskScheduleEventRepository
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase
import kotlinx.datetime.Clock


class AddTaskScheduleEventUseCaseImpl(
    private val transactionProvider: TransactionProvider,
    private val getTaskModelUseCase: GetTaskModelUseCase,
    private val localTaskRepository: LocalTaskRepository,
    private val localTaskScheduleEventRepository: LocalTaskScheduleEventRepository,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
    private val notificationContentMapper: NotificationContentMapper,
    private val appAlarmScheduler: AppAlarmScheduler,
    private val clock: Clock = Clock.System,
) : AddTaskScheduleEventUseCase {
    override suspend fun invoke(
        taskId: Long,
        addTaskScheduleEventModel: AddTaskScheduleEventModel?,
    ): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "AddTaskScheduleEventUseCaseImpl"
        ) {
            transactionProvider.runAsTransaction {
                val now = clock.now()
                val taskModel = getTaskModelUseCase(taskId = taskId)
                    ?: return@runAsTransaction AppResult.Error(error = RoomDatabaseError.NO_TASK_FOUND)

                if (addTaskScheduleEventModel == null) {
                    localTaskRepository.upsert(
                        taskModel = taskModel.copy(
                            taskSchedule = null,
                            updatedAt = now,
                        ),
                    )
                    return@runAsTransaction AppResult.Success(Unit)
                }


                if (addTaskScheduleEventModel.startDateInEpochDays != null) {
                    val taskScheduleEventModel = addTaskScheduleEventModel
                        .toTaskScheduleEventModel()
                        .copy(
                            taskId = taskId,
                            createdAt = now
                        )

                    val taskScheduleEventId = localTaskScheduleEventRepository
                        .insert(
                            taskScheduleEventModel = taskScheduleEventModel
                        )
                    val updatedTaskModel = taskModel.copy(
                        taskSchedule = taskScheduleEventModel
                            .copy(
                                taskScheduleEventId = taskScheduleEventId
                            ),
                        updatedAt = now
                    )
                    if (taskModel.taskSchedule?.isNotificationEnabled == true) {
                        appAlarmScheduler.cancel(
                            notificationId = taskModel.taskId.toInt(),
                            taskId = taskModel.taskId
                        )
                    }
                    if (updatedTaskModel.taskSchedule?.isNotificationEnabled == true) {
                        notificationContentMapper(task = updatedTaskModel)?.let { notificationContent ->
                            appAlarmScheduler.schedule(
                                notificationId = notificationContent.notificationId,
                                taskId = notificationContent.taskId,
                                triggerAtMillis = notificationContent.triggerInstant.toEpochMilliseconds()
                            )
                        }
                    }

                    localTaskRepository
                        .upsert(
                            taskModel = updatedTaskModel
                        )

                    return@runAsTransaction AppResult.Success(Unit)
                } else {

                    return@runAsTransaction AppResult.Error(error = RoomDatabaseError.UNKNOWN)
                }


            }
        }
    }
}