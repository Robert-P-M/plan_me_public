package at.robthered.plan_me.features.common.domain.use_case

import at.robthered.plan_me.features.common.domain.model.NotificationContent
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel

interface NotificationContentMapper {
    operator fun invoke(
        task: TaskModel,
    ): NotificationContent?
}