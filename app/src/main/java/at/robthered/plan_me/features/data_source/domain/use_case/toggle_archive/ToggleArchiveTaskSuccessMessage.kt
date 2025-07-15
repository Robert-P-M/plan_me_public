package at.robthered.plan_me.features.data_source.domain.use_case.toggle_archive

import at.robthered.plan_me.features.common.domain.SuccessMessage

enum class ToggleArchiveTaskSuccessMessage : SuccessMessage {
    TASK_ARCHIVED,
    TASK_UN_ARCHIVED,
}