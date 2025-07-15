package at.robthered.plan_me.features.data_source.domain.use_case.combine_tasks_with_hashtags

import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.TaskWithUiHashtagsModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart

class CombineTasksWithHashtagsUseCaseImpl(
    private val localHashtagRepository: LocalHashtagRepository,
) : CombineTasksWithHashtagsUseCase {
    override operator fun invoke(
        tasks: List<TaskModel>,
    ): Flow<List<TaskWithUiHashtagsModel>> {
        return if (tasks.isEmpty()) {
            flowOf(emptyList())
        } else {

            val hashtagFlows = tasks.map { task ->
                localHashtagRepository.getHashtagsForTask(taskId = task.taskId)
                    .onStart { emit(emptyList()) }
            }
            combine(hashtagFlows) { arrayOfHashtagLists ->
                tasks.mapIndexed { index, task ->
                    TaskWithUiHashtagsModel(
                        task = task,
                        hashtags = arrayOfHashtagLists[index].map {
                            UiHashtagModel.ExistingHashtagModel(
                                hashtagId = it.hashtagId,
                                name = it.name
                            )
                        }
                    )
                }
            }
        }
    }
}