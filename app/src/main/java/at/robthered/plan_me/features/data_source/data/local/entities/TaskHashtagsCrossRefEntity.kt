package at.robthered.plan_me.features.data_source.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["task_id", "hashtag_id"],
    tableName = "task_hashtags_cross_ref",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["task_id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = HashtagEntity::class,
            parentColumns = ["hashtag_id"],
            childColumns = ["hashtag_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(
            value = ["task_id"]
        ),
        Index(
            value = ["hashtag_id"]
        )
    ]
)
data class TaskHashtagsCrossRefEntity(
    @ColumnInfo(name = "task_id")
    val taskId: Long,
    @ColumnInfo(name = "hashtag_id")
    val hashtagId: Long,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
)