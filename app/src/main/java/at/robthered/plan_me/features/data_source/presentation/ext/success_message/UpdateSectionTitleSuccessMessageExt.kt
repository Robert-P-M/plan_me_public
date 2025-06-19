package at.robthered.plan_me.features.data_source.presentation.ext.success_message

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.update_section_title.UpdateSectionTitleSuccessMessage

fun UpdateSectionTitleSuccessMessage.toUiText(): UiText {
    return when (this) {
        UpdateSectionTitleSuccessMessage.SECTION_TITLE_UPDATED -> UiText
            .StringResource(
                id = R.string.update_section_title_success_message_section_title_updated
            )
    }
}