package at.robthered.plan_me.features.data_source.domain.use_case.load_move_task_items

import at.robthered.plan_me.features.data_source.domain.model.move_task.MoveTaskModel
import at.robthered.plan_me.features.data_source.domain.model.move_task.MoveTaskModelRootEnum
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.use_case.flattened_recurisve_task_loader.FlattenedRecursiveTaskLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

@OptIn(ExperimentalCoroutinesApi::class)
class LoadMoveTaskItemsUseCaseImpl(
    private val localTaskRepository: LocalTaskRepository,
    private val localSectionRepository: LocalSectionRepository,
    private val flattenedRecursiveTaskLoader: FlattenedRecursiveTaskLoader,
) : LoadMoveTaskItemsUseCase {

    override operator fun invoke(depth: Int): Flow<List<MoveTaskModel>> {

        val sectionsHierarchyFlow: Flow<List<MoveTaskModel>> = localSectionRepository.get()
            .flatMapLatest { sectionsList ->
                if (sectionsList.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    val flowsForEachSection: List<Flow<List<MoveTaskModel>>> =
                        sectionsList.map { sectionModel ->
                            localTaskRepository.getTaskModelsForSection(sectionId = sectionModel.sectionId)
                                .flatMapLatest { tasksInSection ->
                                    flow {
                                        val sectionItems = mutableListOf<MoveTaskModel>()
                                        sectionItems.add(
                                            MoveTaskModel.Section(
                                                title = sectionModel.title,
                                                sectionId = sectionModel.sectionId
                                            )
                                        )
                                        tasksInSection.forEach { taskWCM ->
                                            sectionItems.add(
                                                MoveTaskModel.Task(
                                                    title = taskWCM.title,
                                                    taskId = taskWCM.taskId,
                                                    depth = 1
                                                )
                                            )
                                            sectionItems.addAll(
                                                flattenedRecursiveTaskLoader(
                                                    parentTaskId = taskWCM.taskId,
                                                    currentVisualDepth = 2,
                                                    maxRecursionLevels = depth - 1
                                                )
                                            )
                                        }
                                        emit(sectionItems)
                                    }
                                }
                        }
                    if (flowsForEachSection.isEmpty()) {
                        flowOf(emptyList())
                    } else {
                        combine(flowsForEachSection) { listOfLists ->
                            listOfLists.toList().flatten()
                        }
                    }
                }
            }

        val rootTasksHierarchyFlow: Flow<List<MoveTaskModel>> =
            localTaskRepository.getRootTaskModels()
                .flatMapLatest { rootTasksList ->
                    if (rootTasksList.isEmpty()) {
                        flowOf(emptyList())
                    } else {
                        flow {
                            val allRootTaskItems = mutableListOf<MoveTaskModel>()
                            rootTasksList.forEach { rootTaskWCM ->
                                allRootTaskItems.add(
                                    MoveTaskModel.Task(
                                        title = rootTaskWCM.title,
                                        taskId = rootTaskWCM.taskId,
                                        depth = 0
                                    )
                                )
                                allRootTaskItems.addAll(
                                    flattenedRecursiveTaskLoader(
                                        parentTaskId = rootTaskWCM.taskId,
                                        currentVisualDepth = 1,
                                        maxRecursionLevels = depth - 1
                                    )
                                )
                            }
                            emit(allRootTaskItems)
                        }
                    }
                }

        return combine(
            sectionsHierarchyFlow,
            rootTasksHierarchyFlow
        ) { sectionsItems, rootTaskItems ->
            buildList {
                add(MoveTaskModel.Root(MoveTaskModelRootEnum.INBOX))
                addAll(sectionsItems)
                addAll(rootTaskItems)
            }
        }
            .flowOn(Dispatchers.Default)
    }
}