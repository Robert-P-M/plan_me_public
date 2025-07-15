package at.robthered.plan_me.features.data_source.domain.model.add_task_schedule_event

import android.os.Parcelable
import at.robthered.plan_me.features.data_source.domain.validation.TaskScheduleDateEpochDaysError
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddTaskScheduleEventModelError(
    val startDateInEpochDays: TaskScheduleDateEpochDaysError? = null,
) : Parcelable