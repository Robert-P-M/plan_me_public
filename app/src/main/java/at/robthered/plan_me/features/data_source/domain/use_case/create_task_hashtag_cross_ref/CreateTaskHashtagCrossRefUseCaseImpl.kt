package at.robthered.plan_me.features.data_source.domain.use_case.create_task_hashtag_cross_ref

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.models.TaskHashtagsCrossRefModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskHashtagsCrossRefRepository
import at.robthered.plan_me.features.data_source.domain.use_case.add_hashtag_helper.AddHashtagHelper
import kotlinx.datetime.Clock

class CreateTaskHashtagCrossRefUseCaseImpl(
    private val addHashtagHelper: AddHashtagHelper,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
    private val localTaskHashtagsCrossRefRepository: LocalTaskHashtagsCrossRefRepository,
    private val clock: Clock = Clock.System,
) : CreateTaskHashtagCrossRefUseCase {
    override suspend operator fun invoke(
        taskId: Long,
        hashtags: List<UiHashtagModel>,
    ): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall {


            val now = clock.now()
            val newHashtagModels =
                hashtags.filterIsInstance<UiHashtagModel.NewHashTagModel>()
            val existingHashtagModels =
                hashtags.filterIsInstance<UiHashtagModel.ExistingHashtagModel>()
            if (newHashtagModels.isNotEmpty()) {
                val ids = addHashtagHelper(uiHashtagModels = newHashtagModels)
                val crossRefs = ids.map { id ->
                    TaskHashtagsCrossRefModel(
                        taskId = taskId,
                        hashtagId = id,
                        createdAt = now
                    )
                }

                localTaskHashtagsCrossRefRepository
                    .insert(crossRefs = crossRefs)

            }
            if (existingHashtagModels.isNotEmpty()) {
                val crossRefs = existingHashtagModels.map {
                    TaskHashtagsCrossRefModel(
                        taskId = taskId,
                        hashtagId = it.hashtagId,
                        createdAt = now
                    )
                }
                localTaskHashtagsCrossRefRepository
                    .insert(crossRefs = crossRefs)
            }
            AppResult.Success(Unit)
        }
    }
}