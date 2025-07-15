package at.robthered.plan_me.features.data_source.domain.model

import at.robthered.plan_me.features.data_source.domain.local.models.SectionModel

sealed interface InboxScreenUiModel {
    data class Tasks(val tasks: List<TaskWithHashtagsAndChildrenModel>) :
        InboxScreenUiModel

    data class Section(
        val section: SectionModel,
        val tasks: List<TaskWithHashtagsAndChildrenModel>,
    ) : InboxScreenUiModel
}