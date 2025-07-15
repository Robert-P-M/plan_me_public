package at.robthered.plan_me.features.data_source.domain.use_case.toggle_complete

import at.robthered.plan_me.features.common.domain.LoadingStatus

enum class ToggleCompleteTaskLoadingStatus : LoadingStatus {
    SWITCH_TO_COMPLETED,
    SWITCH_TO_UNCOMPLETED
}