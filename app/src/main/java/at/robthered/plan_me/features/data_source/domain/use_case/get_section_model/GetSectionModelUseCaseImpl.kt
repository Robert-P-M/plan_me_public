package at.robthered.plan_me.features.data_source.domain.use_case.get_section_model

import at.robthered.plan_me.features.data_source.domain.local.models.SectionModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository
import kotlinx.coroutines.flow.first

class GetSectionModelUseCaseImpl(
    private val localSectionRepository: LocalSectionRepository,
) : GetSectionModelUseCase {
    override suspend fun invoke(sectionId: Long): SectionModel? {
        return localSectionRepository
            .get(sectionId = sectionId)
            .first()
    }
}