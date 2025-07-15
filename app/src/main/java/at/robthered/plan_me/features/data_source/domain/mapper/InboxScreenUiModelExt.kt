package at.robthered.plan_me.features.data_source.domain.mapper

import at.robthered.plan_me.features.data_source.domain.model.InboxScreenUiModel
import at.robthered.plan_me.features.data_source.domain.model.SectionWithTasksModel
import at.robthered.plan_me.features.data_source.domain.model.TaskWithHashtagsAndChildrenModel

fun SectionWithTasksModel.toInboxItem(): InboxScreenUiModel {
    return InboxScreenUiModel.Section(
        section = this.section,
        tasks = this.tasks
    )
}

fun List<TaskWithHashtagsAndChildrenModel>.toInboxItem(): InboxScreenUiModel {
    return InboxScreenUiModel.Tasks(
        tasks = this
    )
}