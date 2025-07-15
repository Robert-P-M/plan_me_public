package at.robthered.plan_me.features.data_source.domain.use_case.update_hashtag_name

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.model.update_hashtag.UpdateHashtagModel

interface UpdateHashtagNameUseCase {
    suspend operator fun invoke(updateHashtagModel: UpdateHashtagModel): AppResult<Unit, RoomDatabaseError>
}