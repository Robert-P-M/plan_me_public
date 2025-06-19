package at.robthered.plan_me.features.data_source.domain.model.add_task_schedule_event

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddTaskScheduleEventModel(
    val taskId: Long = 0,
    val startDateInEpochDays: Int? = null,
    val timeOfDayInMinutes: Int? = null,
    val isNotificationEnabled: Boolean = false,
    val durationInMinutes: Int? = null,
    val isFullDay: Boolean = false,
) : Parcelable