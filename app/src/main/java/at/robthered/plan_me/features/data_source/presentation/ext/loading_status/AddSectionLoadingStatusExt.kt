package at.robthered.plan_me.features.data_source.presentation.ext.loading_status

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.add_section.AddSectionLoadingStatus

fun AddSectionLoadingStatus.toUiText(): UiText {
    return when (this) {
        AddSectionLoadingStatus.STARTING -> UiText
            .StringResource(
                id = R.string.add_section_loading_starting
            )
    }
}