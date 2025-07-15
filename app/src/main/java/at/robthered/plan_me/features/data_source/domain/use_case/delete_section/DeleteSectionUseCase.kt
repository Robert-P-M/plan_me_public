package at.robthered.plan_me.features.data_source.domain.use_case.delete_section

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError

interface DeleteSectionUseCase {
    suspend operator fun invoke(sectionId: Long): AppResult<Unit, RoomDatabaseError>
}