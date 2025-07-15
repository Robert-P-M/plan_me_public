package at.robthered.plan_me.features.data_source.data.local.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TaskWithHashtagsRelation(
    @Embedded
    val task: TaskEntity,
    @Relation(
        parentColumn = "task_id",
        entityColumn = "hashtag_id",
        associateBy = Junction(TaskHashtagsCrossRefEntity::class),
        entity = HashtagEntity::class
    )
    val hashtags: List<HashtagEntity>,
)