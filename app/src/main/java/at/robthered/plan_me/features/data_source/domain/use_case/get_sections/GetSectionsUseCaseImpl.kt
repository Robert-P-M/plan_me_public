package at.robthered.plan_me.features.data_source.domain.use_case.get_sections

import at.robthered.plan_me.features.data_source.domain.local.models.SectionModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository
import kotlinx.coroutines.flow.Flow

class GetSectionsUseCaseImpl(
    private val localSectionRepository: LocalSectionRepository,
) : GetSectionsUseCase {
    override operator fun invoke(): Flow<List<SectionModel>> {
        return localSectionRepository
            .get()
    }
}