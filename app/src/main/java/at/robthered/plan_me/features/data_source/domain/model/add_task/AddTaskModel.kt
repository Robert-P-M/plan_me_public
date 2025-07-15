package at.robthered.plan_me.features.data_source.domain.model.add_task

import android.os.Parcelable
import at.robthered.plan_me.features.data_source.domain.model.add_task_schedule_event.AddTaskScheduleEventModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddTaskModel(
    val sectionId: Long? = null,
    val parentTaskId: Long? = null,
    val title: String = "",
    val description: String? = null,
    val taskSchedule: AddTaskScheduleEventModel? = null,
    val priorityEnum: PriorityEnum? = null,
    val hashtags: List<UiHashtagModel> = emptyList(),
) : Parcelable