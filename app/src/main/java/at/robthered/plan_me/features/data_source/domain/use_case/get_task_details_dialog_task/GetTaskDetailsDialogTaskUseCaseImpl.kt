package at.robthered.plan_me.features.data_source.domain.use_case.get_task_details_dialog_task

import at.robthered.plan_me.features.common.domain.AppResource
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.error.TaskNotFoundException
import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.model.TaskWithHashtagsAndChildrenModel
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_by_id.GetTaskByIdUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.recursive_task_with_hashtags_and_children.RecursiveTaskWithHashtagsAndChildrenModelHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class GetTaskDetailsDialogTaskUseCaseImpl(
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val recursiveTaskWithHashtagsAndChildrenModelHelper: RecursiveTaskWithHashtagsAndChildrenModelHelper,
) : GetTaskDetailsDialogTaskUseCase {

    @OptIn(ExperimentalCoroutinesApi::class)
    override operator fun invoke(
        taskId: Long,
        depth: Int,
        showCompleted: Boolean?,
        showArchived: Boolean?,
        sortDirection: SortDirection,
    ): Flow<AppResource<TaskWithHashtagsAndChildrenModel>> {

        return getTaskByIdUseCase(
            taskId = taskId,
        )
            .flatMapLatest { mainTaskWithHashtagsNullable ->

                if (mainTaskWithHashtagsNullable == null) {
                    flow { throw TaskNotFoundException(taskId = taskId) }
                } else {
                    recursiveTaskWithHashtagsAndChildrenModelHelper(
                        parentTaskId = mainTaskWithHashtagsNullable.task.taskId,
                        currentDepth = depth - 1,
                        showCompleted = showCompleted,
                        showArchived = showArchived,
                        sortDirection = sortDirection
                    ).map { childrenList ->
                        TaskWithHashtagsAndChildrenModel(
                            taskWithUiHashtagsModel = mainTaskWithHashtagsNullable,
                            children = childrenList,
                            maxDepthReached = (depth - 1) < 0
                        )
                    }
                }


            }
            .map<TaskWithHashtagsAndChildrenModel, AppResource<TaskWithHashtagsAndChildrenModel>> { data ->
                AppResource.Success(data)
            }
            .onStart {
                emit(AppResource.Loading())
            }
            .catch { e ->
                e.printStackTrace()
                val errorState = if (e is TaskNotFoundException) {
                    AppResource.Error(RoomDatabaseError.NO_TASK_FOUND)
                } else {
                    AppResource.Error(RoomDatabaseError.UNKNOWN)
                }
                emit(errorState)
            }
            .flowOn(Dispatchers.Default)


    }
}