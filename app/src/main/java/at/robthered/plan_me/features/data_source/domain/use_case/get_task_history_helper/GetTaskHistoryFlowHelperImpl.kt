package at.robthered.plan_me.features.data_source.domain.use_case.get_task_history_helper

import at.robthered.plan_me.features.data_source.domain.model.task_statistics.TaskHistoryModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskArchivedHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskCompletedHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskDescriptionHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskPriorityHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskScheduleEventRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskTitleHistoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class GetTaskHistoryFlowHelperImpl(
    private val localTaskTitleHistoryRepository: LocalTaskTitleHistoryRepository,
    private val localTaskDescriptionHistoryRepository: LocalTaskDescriptionHistoryRepository,
    private val localTaskPriorityHistoryRepository: LocalTaskPriorityHistoryRepository,
    private val localTaskCompletedHistoryRepository: LocalTaskCompletedHistoryRepository,
    private val localTaskArchivedHistoryRepository: LocalTaskArchivedHistoryRepository,
    private val localTaskScheduleEventRepository: LocalTaskScheduleEventRepository,
) : GetTaskHistoryFlowHelper {
    @OptIn(ExperimentalCoroutinesApi::class)
    override operator fun invoke(taskId: Long): Flow<TaskHistoryModel> {
        return combine(
            localTaskTitleHistoryRepository.getForTask(taskId),
            localTaskDescriptionHistoryRepository.getForTask(taskId),
            localTaskPriorityHistoryRepository.getForTask(taskId),
            localTaskCompletedHistoryRepository.getForTask(taskId),
            localTaskArchivedHistoryRepository.getForTask(taskId),
        ) { title, description, priority, completed, archived ->
            TaskHistoryModel(title, description, priority, completed, archived)
        }.flatMapLatest { history ->
            combine(
                flowOf(history),
                localTaskScheduleEventRepository.getForTask(taskId)
            ) { taskHistory, taskSchedules ->
                taskHistory.copy(
                    taskSchedules = taskSchedules
                )
            }
        }
    }
}