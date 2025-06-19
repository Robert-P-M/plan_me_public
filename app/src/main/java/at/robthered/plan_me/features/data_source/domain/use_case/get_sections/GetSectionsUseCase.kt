package at.robthered.plan_me.features.data_source.domain.use_case.get_sections

import at.robthered.plan_me.features.data_source.domain.local.models.SectionModel
import kotlinx.coroutines.flow.Flow

interface GetSectionsUseCase {
    operator fun invoke(): Flow<List<SectionModel>>
}