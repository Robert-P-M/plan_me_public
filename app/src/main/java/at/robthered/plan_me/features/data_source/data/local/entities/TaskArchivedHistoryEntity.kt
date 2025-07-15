package at.robthered.plan_me.features.data_source.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "task_archived_history",
    indices = [
        Index(
            value = ["task_id"]
        )
    ],
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["task_id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TaskArchivedHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "task_archived_history_id")
    val taskArchivedHistoryId: Long = 0,
    @ColumnInfo(name = "task_id")
    val taskId: Long,
    @ColumnInfo(name = "is_archived")
    val isArchived: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
)