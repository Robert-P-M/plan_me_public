package at.robthered.plan_me.features.data_source.domain.use_case.load_task_roots

import at.robthered.plan_me.features.data_source.domain.model.task_tree.TaskTreeModel
import at.robthered.plan_me.features.data_source.domain.model.task_tree.TaskTreeModelRootEnum
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class LoadTaskRootsUseCaseImpl(
    private val localTaskRepository: LocalTaskRepository,
    private val localSectionRepository: LocalSectionRepository,
) : LoadTaskRootsUseCase {
    @OptIn(ExperimentalCoroutinesApi::class)
    override operator fun invoke(taskId: Long): Flow<List<TaskTreeModel>> {
        return localTaskRepository.getTaskModelById(taskId = taskId)
            .flatMapLatest { currentTaskModelNullable ->
                if (currentTaskModelNullable == null) {
                    flowOf(
                        listOf<TaskTreeModel>(
                            TaskTreeModel.Root(
                                taskTreeModelRootEnum = TaskTreeModelRootEnum.INBOX
                            )
                        )
                    )
                } else {
                    flow {
                        val path = mutableListOf<TaskTreeModel>()

                        path.add(
                            TaskTreeModel.Task(
                                currentTaskModelNullable.title,
                                currentTaskModelNullable.taskId
                            )
                        )

                        var currentParentIdToProcess: Long? = currentTaskModelNullable.parentTaskId
                        var sectionIdForCurrentHierarchy: Long? = currentTaskModelNullable.sectionId

                        while (currentParentIdToProcess != null) {
                            val parentTaskModel =
                                localTaskRepository.getTaskModelById(currentParentIdToProcess)
                                    .first()
                            if (parentTaskModel != null) {
                                path.add(
                                    TaskTreeModel.Task(
                                        parentTaskModel.title,
                                        parentTaskModel.taskId
                                    )
                                )
                                sectionIdForCurrentHierarchy = parentTaskModel.sectionId
                                currentParentIdToProcess = parentTaskModel.parentTaskId
                            } else {
                                currentParentIdToProcess = null
                            }
                        }

                        if (sectionIdForCurrentHierarchy != null) {
                            val sectionModel =
                                localSectionRepository.get(sectionIdForCurrentHierarchy).first()
                            sectionModel?.let {
                                path.add(TaskTreeModel.Section(it.title, it.sectionId))
                            }
                        }

                        path.add(
                            TaskTreeModel.Root(
                                taskTreeModelRootEnum = TaskTreeModelRootEnum.INBOX
                            )
                        )

                        emit(path.reversed())
                    }
                }
            }
    }
}