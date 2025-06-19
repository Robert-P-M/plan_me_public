package at.robthered.plan_me.features.data_source.domain.repository

import at.robthered.plan_me.features.data_source.domain.local.models.HashtagWithTasksModel
import kotlinx.coroutines.flow.Flow

interface LocalHashtagWithTasksRelationRepository {

    fun getHashtagWithTasks(hashtagId: Long): Flow<HashtagWithTasksModel?>

}