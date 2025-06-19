package at.robthered.plan_me.features.data_source.presentation.ext.success_message

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.delete_section.DeleteSectionSuccessMessage

fun DeleteSectionSuccessMessage.toUiText(): UiText {
    return when (this) {
        DeleteSectionSuccessMessage.SECTION_DELETED_SUCCESS -> UiText
            .StringResource(
                id = R.string.delete_section_message_section_deleted_success
            )
    }
}