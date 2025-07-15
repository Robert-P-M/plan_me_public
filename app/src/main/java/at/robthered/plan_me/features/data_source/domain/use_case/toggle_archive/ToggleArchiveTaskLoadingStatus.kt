package at.robthered.plan_me.features.data_source.domain.use_case.toggle_archive

import at.robthered.plan_me.features.common.domain.LoadingStatus

enum class ToggleArchiveTaskLoadingStatus : LoadingStatus {
    SWITCH_TO_ARCHIVED,
    SWITCH_TO_UN_ARCHIVED,
}