package at.robthered.plan_me.features.data_source.domain.use_case.delete_task_hashtag_reference

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError

interface DeleteTaskHashtagReferenceUseCase {
    suspend operator fun invoke(taskId: Long, hashtagId: Long): AppResult<Unit, RoomDatabaseError>
}