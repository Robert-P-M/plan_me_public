package at.robthered.plan_me.features.data_source.data.local.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class HashtagWithTasksRelation(
    @Embedded val hashtag: HashtagEntity,
    @Relation(
        parentColumn = "hashtag_id",
        entityColumn = "task_id",
        associateBy = Junction(TaskHashtagsCrossRefEntity::class),
        entity = TaskEntity::class
    )
    val tasks: List<TaskEntity> = emptyList(),
)