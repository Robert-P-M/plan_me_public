package at.robthered.plan_me.features.data_source.domain.model

import at.robthered.plan_me.features.data_source.domain.local.models.SectionModel

data class SectionWithTasksModel(
    val section: SectionModel,
    val tasks: List<TaskWithHashtagsAndChildrenModel>,
)