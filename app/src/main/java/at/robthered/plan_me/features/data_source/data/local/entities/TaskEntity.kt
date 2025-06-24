package at.robthered.plan_me.features.data_source.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum

@Entity(
    tableName = "task",
    indices = [
        Index(
            value = ["section_id"]
        ),
        Index(
            value = ["parent_task_id"]
        ),
        Index(
            value = ["current_task_schedule_event_task_id"]
        )
    ],
    foreignKeys = [
        ForeignKey(
            entity = SectionEntity::class,
            parentColumns = ["section_id"],
            childColumns = ["section_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["task_id"],
            childColumns = ["parent_task_id"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "task_id")
    val taskId: Long = 0L,
    @ColumnInfo(name = "parent_task_id")
    val parentTaskId: Long? = null,
    @ColumnInfo(name = "section_id")
    val sectionId: Long? = null,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String? = null,
    @ColumnInfo(name = "priority_enum")
    val priorityEnum: PriorityEnum? = null,
    @ColumnInfo(name = "completed")
    val isCompleted: Boolean = false,
    @ColumnInfo(name = "archived")
    val isArchived: Boolean = false,
    @Embedded(prefix = "current_task_schedule_event_")
    val taskSchedule: TaskScheduleEventEntity? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
)