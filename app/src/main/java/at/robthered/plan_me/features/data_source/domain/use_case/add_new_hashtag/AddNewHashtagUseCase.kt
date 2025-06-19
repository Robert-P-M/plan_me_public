package at.robthered.plan_me.features.data_source.domain.use_case.add_new_hashtag

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel

interface AddNewHashtagUseCase {
    suspend operator fun invoke(
        taskId: Long,
        uiHashtagModel: UiHashtagModel.NewHashTagModel,
    ): AppResult<Unit, RoomDatabaseError>
}