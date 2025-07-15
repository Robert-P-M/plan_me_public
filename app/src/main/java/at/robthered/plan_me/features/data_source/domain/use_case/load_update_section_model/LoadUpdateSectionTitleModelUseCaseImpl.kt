package at.robthered.plan_me.features.data_source.domain.use_case.load_update_section_model

import at.robthered.plan_me.features.data_source.data.local.mapper.toUpdateSectionModel
import at.robthered.plan_me.features.data_source.domain.model.update_section_title.UpdateSectionTitleModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LoadUpdateSectionTitleModelUseCaseImpl(
    private val localSectionRepository: LocalSectionRepository,
) : LoadUpdateSectionTitleModelUseCase {
    override suspend operator fun invoke(sectionId: Long): Flow<UpdateSectionTitleModel> {
        return localSectionRepository.get(sectionId = sectionId).map {

            it?.toUpdateSectionModel()
                ?: UpdateSectionTitleModel()
        }
    }
}