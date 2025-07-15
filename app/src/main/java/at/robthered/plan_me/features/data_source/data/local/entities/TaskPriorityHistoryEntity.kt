package at.robthered.plan_me.features.data_source.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum

@Entity(
    tableName = "task_priority_history",
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
    ],
)
data class TaskPriorityHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "task_priority_history_id")
    val taskPriorityId: Long = 0L,
    @ColumnInfo(name = "task_id")
    val taskId: Long,
    @ColumnInfo(name = "priority_enum")
    val priorityEnum: PriorityEnum? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
)