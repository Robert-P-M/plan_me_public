package at.robthered.plan_me.features.data_source.domain.use_case.toggle_complete

import at.robthered.plan_me.features.common.domain.SuccessMessage

enum class ToggleCompleteTaskSuccessMessage : SuccessMessage {
    TASK_COMPLETED,
    TASK_UN_COMPLETED
}