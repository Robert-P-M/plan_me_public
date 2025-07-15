package at.robthered.plan_me.features.data_source.presentation.ext.success_message

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.add_section.AddSectionSuccessMessage

fun AddSectionSuccessMessage.toUiText(): UiText {
    return when (this) {
        AddSectionSuccessMessage.SECTION_SAVED -> UiText
            .StringResource(
                id = R.string.add_section_message_section_saved
            )
    }
}