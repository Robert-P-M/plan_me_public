package at.robthered.plan_me.features.data_source.domain.use_case.update_section_title

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.model.update_section_title.UpdateSectionTitleModel

interface UpdateSectionTitleUseCase {
    suspend operator fun invoke(updateSectionTitleModel: UpdateSectionTitleModel): AppResult<Unit, RoomDatabaseError>
}