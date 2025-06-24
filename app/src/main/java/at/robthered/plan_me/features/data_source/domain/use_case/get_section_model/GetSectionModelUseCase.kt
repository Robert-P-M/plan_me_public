package at.robthered.plan_me.features.data_source.domain.use_case.get_section_model

import at.robthered.plan_me.features.data_source.domain.local.models.SectionModel

interface GetSectionModelUseCase {
    suspend operator fun invoke(sectionId: Long): SectionModel?
}