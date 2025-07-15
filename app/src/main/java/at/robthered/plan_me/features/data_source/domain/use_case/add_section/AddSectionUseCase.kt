package at.robthered.plan_me.features.data_source.domain.use_case.add_section

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.model.add_section.AddSectionModel

interface AddSectionUseCase {
    suspend operator fun invoke(addSectionModel: AddSectionModel): AppResult<Unit, RoomDatabaseError>
}