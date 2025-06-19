package at.robthered.plan_me.features.data_source.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "task_schedule_event",
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
data class TaskScheduleEventEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "task_schedule_event_id")
    val taskScheduleEventId: Long = 0,
    @ColumnInfo(name = "task_id")
    val taskId: Long,
    @ColumnInfo(name = "start_date_in_epoch_days")
    val startDateInEpochDays: Int,
    @ColumnInfo(name = "time_of_day_in_minutes")
    val timeOfDayInMinutes: Int? = null,
    @ColumnInfo(name = "is_notification_enabled")
    val isNotificationEnabled: Boolean = false,
    @ColumnInfo(name = "duration_in_minutes")
    val durationInMinutes: Int? = null,
    @ColumnInfo(name = "is_full_day")
    val isFullDay: Boolean = false,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
)