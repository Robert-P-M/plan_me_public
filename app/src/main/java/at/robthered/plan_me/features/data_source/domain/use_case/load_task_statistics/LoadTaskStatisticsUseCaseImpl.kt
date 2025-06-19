package at.robthered.plan_me.features.data_source.domain.use_case.load_task_statistics

import at.robthered.plan_me.features.common.domain.AppResource
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.data.local.exception.DatabaseOperationFailedException
import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.model.task_statistics.TaskStatisticsModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskHashtagsCrossRefRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_history_helper.GetTaskHistoryFlowHelper
import at.robthered.plan_me.features.data_source.domain.use_case.task_statistics.TaskStatisticsBuilder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class LoadTaskStatisticsUseCaseImpl(
    private val taskStatisticsBuilder: TaskStatisticsBuilder,
    private val getTaskHistoryFlowHelper: GetTaskHistoryFlowHelper,
    private val localTaskRepository: LocalTaskRepository,
    private val localTaskHashtagsCrossRefRepository: LocalTaskHashtagsCrossRefRepository,
) : LoadTaskStatisticsUseCase {
    override operator fun invoke(taskId: Long): Flow<AppResource<List<TaskStatisticsModel>>> {

        val taskCoreDataFlow = combine(
            localTaskRepository.getTaskModelById(taskId),
            localTaskRepository.getTaskModelsForParent(
                parentTaskId = taskId,
                sortDirection = SortDirection.DESC,
            ),
        ) { taskModel, subTasks ->
            if (taskModel == null) null else
                Pair(taskModel, subTasks)
        }

        val finalHashtagsFlow = combine(
            flow = localTaskHashtagsCrossRefRepository.getForTaskId(
                taskId = taskId,
            )
                .map { it?.hashtags ?: emptyList() },
            flow2 = localTaskHashtagsCrossRefRepository.getCrossRefForTask(taskId)
        ) { hashtagModelsFromRelation, crossRefEntities ->

            hashtagModelsFromRelation.map { model ->
                model
                    .copy(
                        createdAt = crossRefEntities.find {
                            it.hashtagId == model.hashtagId
                        }?.createdAt
                            ?: model.createdAt
                    )
            }

        }

        return combine(
            getTaskHistoryFlowHelper(taskId = taskId),
            taskCoreDataFlow,
            finalHashtagsFlow,
        ) { historyData, taskCoreNullable, finalHashtags ->
            val taskCore = taskCoreNullable
                ?: return@combine AppResource.Error(RoomDatabaseError.NO_TASK_FOUND)
            val (taskModel, subTasks) = taskCore
            val statisticsItems = taskStatisticsBuilder(
                historyData = historyData,
                taskModel = taskModel,
                subTasks = subTasks,
                finalHashtagModels = finalHashtags
            ).sortedByDescending { it.createdAt }
            AppResource.Success(statisticsItems)
        }.onStart {
            emit(AppResource.Loading())
        }.catch { e ->
            when (e) {
                is DatabaseOperationFailedException -> {
                    emit(AppResource.Error(e.error))
                }

                is CancellationException -> {
                    e.printStackTrace()
                    emit(AppResource.Error(error = RoomDatabaseError.UNKNOWN))
                }

                else -> {
                    e.printStackTrace()
                    emit(AppResource.Error(error = RoomDatabaseError.UNKNOWN))
                }
            }
        }
            .flowOn(Dispatchers.Default)

    }
}