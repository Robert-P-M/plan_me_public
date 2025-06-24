package at.robthered.plan_me.features.data_source.domain.use_case.load_update_section_model

import at.robthered.plan_me.features.data_source.domain.model.update_section_title.UpdateSectionTitleModel
import kotlinx.coroutines.flow.Flow

interface LoadUpdateSectionTitleModelUseCase {
    suspend operator fun invoke(sectionId: Long): Flow<UpdateSectionTitleModel>
}