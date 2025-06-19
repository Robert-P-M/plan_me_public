package at.robthered.plan_me.features.data_source.domain.use_case.create_task_hashtag_cross_ref

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel

interface CreateTaskHashtagCrossRefUseCase {
    suspend operator fun invoke(
        taskId: Long,
        hashtags: List<UiHashtagModel>,
    ): AppResult<Unit, RoomDatabaseError>
}