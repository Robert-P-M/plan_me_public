package at.robthered.plan_me.features.data_source.domain.use_case.get_inbox_screen_items

import at.robthered.plan_me.features.common.domain.AppResource
import at.robthered.plan_me.features.data_source.domain.model.InboxScreenUiModel
import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import kotlinx.coroutines.flow.Flow

interface GetInboxScreenItemsUseCase {
    operator fun invoke(
        depth: Int,
        showCompleted: Boolean?,
        showArchived: Boolean?,
        sortDirection: SortDirection,
    ): Flow<AppResource<List<InboxScreenUiModel>>>
}