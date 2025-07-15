package at.robthered.plan_me.features.data_source.domain.model.update_task

import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum

data class UpdateTaskModel(
    val sectionId: Long? = null,
    val parentTaskId: Long? = null,
    val taskId: Long = 0L,
    val title: String = "",
    val description: String? = null,
    val isCompleted: Boolean = false,
    val isArchived: Boolean = false,
    val priorityEnum: PriorityEnum? = null,
    val hashtags: List<UiHashtagModel> = emptyList(),
)