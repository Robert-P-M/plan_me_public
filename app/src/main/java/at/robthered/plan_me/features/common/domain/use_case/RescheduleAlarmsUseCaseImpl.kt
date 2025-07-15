package at.robthered.plan_me.features.common.domain.use_case

import at.robthered.plan_me.features.common.domain.notification.AppAlarmScheduler
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class RescheduleAlarmsUseCaseImpl(
    private val localTaskRepository: LocalTaskRepository,
    private val notificationContentMapper: NotificationContentMapper,
    private val appAlarmScheduler: AppAlarmScheduler,
) : RescheduleAlarmsUseCase {
    override suspend operator fun invoke() {
        val todayEpochDays = Clock.System.todayIn(TimeZone.currentSystemDefault()).toEpochDays()
        val tasksToReschedule =
            localTaskRepository.getUpcomingTasksForAlarm(dateInEpochDays = todayEpochDays)
                .first()

        tasksToReschedule.forEach { task ->
            notificationContentMapper(task = task)?.let { content ->
                appAlarmScheduler.schedule(
                    notificationId = content.notificationId,
                    taskId = content.taskId,
                    triggerAtMillis = content.triggerInstant.toEpochMilliseconds()
                )
            }
        }
    }
}