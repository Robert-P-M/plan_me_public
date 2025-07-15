package at.robthered.plan_me.features.common.domain.use_case

import at.robthered.plan_me.features.common.domain.notification.AppNotifier
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import kotlinx.coroutines.flow.firstOrNull

class ShowNotificationUseCaseImpl(
    private val localTaskRepository: LocalTaskRepository,
    private val appNotifier: AppNotifier,
) : ShowNotificationUseCase {
    override suspend operator fun invoke(taskId: Long) {
        val task = localTaskRepository.getTaskModelById(taskId = taskId).firstOrNull()
        if (task != null && !task.isCompleted && !task.isArchived) {
            appNotifier.show(task = task)
        }
    }
}