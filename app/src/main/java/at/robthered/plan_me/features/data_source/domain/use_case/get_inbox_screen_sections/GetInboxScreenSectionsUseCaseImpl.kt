package at.robthered.plan_me.features.data_source.domain.use_case.get_inbox_screen_sections

import at.robthered.plan_me.features.data_source.domain.model.SectionWithTasksModel
import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.model.TaskWithHashtagsAndChildrenModel
import at.robthered.plan_me.features.data_source.domain.use_case.get_sections.GetSectionsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_models_for_section.GetTaskModelsForSectionUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.recursive_task_with_hashtags_and_children.RecursiveTaskWithHashtagsAndChildrenModelHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class GetInboxScreenSectionsUseCaseImpl(
    private val getSectionsUseCase: GetSectionsUseCase,
    private val getTaskModelsForSectionUseCase: GetTaskModelsForSectionUseCase,
    private val recursiveTaskWithHashtagsAndChildrenModelHelper: RecursiveTaskWithHashtagsAndChildrenModelHelper,
) : GetInboxScreenSectionsUseCase {
    @OptIn(ExperimentalCoroutinesApi::class)
    override operator fun invoke(
        depth: Int,
        showCompleted: Boolean?,
        showArchived: Boolean?,
        sortDirection: SortDirection,
    ): Flow<List<SectionWithTasksModel>> {

        return getSectionsUseCase()
            .flatMapLatest { sections ->
                if (sections.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    val detailedSectionFlows: List<Flow<SectionWithTasksModel>> =
                        sections.map { section ->

                            getTaskModelsForSectionUseCase(
                                sectionId = section.sectionId,
                                showCompleted = showCompleted,
                                showArchived = showArchived,
                                sortDirection = sortDirection
                            )
                                .flatMapLatest { tasksWithHashtags ->
                                    if (tasksWithHashtags.isEmpty()) {
                                        flowOf(
                                            SectionWithTasksModel(
                                                section = section,
                                                tasks = emptyList()
                                            )
                                        )
                                    } else {
                                        val taskWithChildrenFlows: List<Flow<TaskWithHashtagsAndChildrenModel>> =
                                            tasksWithHashtags.map { taskWithHashtags ->
                                                recursiveTaskWithHashtagsAndChildrenModelHelper(
                                                    parentTaskId = taskWithHashtags.task.taskId,
                                                    currentDepth = depth - 1,
                                                    showCompleted = showCompleted,
                                                    showArchived = showArchived,
                                                    sortDirection = sortDirection
                                                ).map { childrenList ->
                                                    TaskWithHashtagsAndChildrenModel(
                                                        taskWithUiHashtagsModel = taskWithHashtags,
                                                        children = childrenList,
                                                        maxDepthReached = (depth - 1) < 0
                                                    )
                                                }
                                            }
                                        combine(taskWithChildrenFlows) { arrayOfTasks ->
                                            SectionWithTasksModel(
                                                section = section,
                                                tasks = arrayOfTasks.toList()
                                            )
                                        }
                                    }
                                }
                        }

                    combine(detailedSectionFlows) { arrayOfDetailedSections ->
                        arrayOfDetailedSections.toList()
                    }
                }


            }
            .flowOn(Dispatchers.Default)
    }
}