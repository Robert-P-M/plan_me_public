package at.robthered.plan_me.features.data_source.domain.use_case.get_inbox_screen_items

import at.robthered.plan_me.features.common.domain.AppResource
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.data.local.exception.DatabaseOperationFailedException
import at.robthered.plan_me.features.data_source.domain.mapper.toInboxItem
import at.robthered.plan_me.features.data_source.domain.model.InboxScreenUiModel
import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.use_case.get_inbox_screen_sections.GetInboxScreenSectionsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_inbox_screen_tasks.GetInboxScreenTasksUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class GetInboxScreenItemsUseCaseImpl(
    private val getInboxScreenTasksUseCase: GetInboxScreenTasksUseCase,
    private val getInboxScreenSectionsUseCase: GetInboxScreenSectionsUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : GetInboxScreenItemsUseCase {
    override operator fun invoke(
        depth: Int,
        showCompleted: Boolean?,
        showArchived: Boolean?,
        sortDirection: SortDirection,
    ): Flow<AppResource<List<InboxScreenUiModel>>> {
        val tasksFlow = getInboxScreenTasksUseCase(
            depth = depth,
            showCompleted = showCompleted,
            showArchived = showArchived,
            sortDirection = sortDirection,
        )
        val sectionsFlow = getInboxScreenSectionsUseCase(
            depth = depth,
            showCompleted = showCompleted,
            showArchived = showArchived,
            sortDirection = sortDirection,
        )
        return combine(
            tasksFlow,
            sectionsFlow,
        ) { inboxTasksResult, inboxSectionsResult ->
            val items = mutableListOf<InboxScreenUiModel>()
            if (inboxTasksResult.isNotEmpty()) {
                items.add(inboxTasksResult.toInboxItem())
            }
            items.addAll(inboxSectionsResult.map { it.toInboxItem() })
            items
        }
            .map<List<InboxScreenUiModel>, AppResource<List<InboxScreenUiModel>>> {
                AppResource.Success(data = it)
            }
            .onStart {
                emit(AppResource.Loading())
            }
            .catch { e ->
                emit(
                    when (e) {
                        is DatabaseOperationFailedException -> {
                            AppResource.Error(e.error)
                        }

                        is CancellationException -> {
                            e.printStackTrace()
                            AppResource.Error(error = RoomDatabaseError.UNKNOWN)
                        }

                        else -> {
                            e.printStackTrace()
                            AppResource.Error(error = RoomDatabaseError.UNKNOWN)
                        }
                    }
                )
            }
            .flowOn(dispatcher)
    }
}