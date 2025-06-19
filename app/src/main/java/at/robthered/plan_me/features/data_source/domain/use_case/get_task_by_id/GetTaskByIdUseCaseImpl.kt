package at.robthered.plan_me.features.data_source.domain.use_case.get_task_by_id

import at.robthered.plan_me.features.data_source.domain.model.TaskWithUiHashtagsModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn

class GetTaskByIdUseCaseImpl(
    private val localTaskRepository: LocalTaskRepository,
    private val localHashtagRepository: LocalHashtagRepository,
) : GetTaskByIdUseCase {
    override operator fun invoke(taskId: Long): Flow<TaskWithUiHashtagsModel?> {
        val taskFlow = localTaskRepository.getTaskModelById(taskId = taskId)

        val hashtagsFlow = localHashtagRepository.getHashtagsForTask(taskId = taskId)

        return combine(
            taskFlow,
            hashtagsFlow
        ) { task, hashtags ->
            task?.let {
                TaskWithUiHashtagsModel(
                    task = it,
                    hashtags = hashtags.map { hashtag ->
                        UiHashtagModel.ExistingHashtagModel(
                            hashtagId = hashtag.hashtagId,
                            name = hashtag.name
                        )
                    }
                )
            }
        }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)
    }
}