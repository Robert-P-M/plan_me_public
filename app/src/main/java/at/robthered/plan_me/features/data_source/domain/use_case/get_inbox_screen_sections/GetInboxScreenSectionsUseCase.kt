package at.robthered.plan_me.features.data_source.domain.use_case.get_inbox_screen_sections

import at.robthered.plan_me.features.data_source.domain.model.SectionWithTasksModel
import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import kotlinx.coroutines.flow.Flow

interface GetInboxScreenSectionsUseCase {
    operator fun invoke(
        depth: Int,
        showCompleted: Boolean?,
        showArchived: Boolean?,
        sortDirection: SortDirection,
    ): Flow<List<SectionWithTasksModel>>
}