package at.robthered.plan_me.features.common.domain.use_case

interface ShowNotificationUseCase {
    suspend operator fun invoke(taskId: Long)
}