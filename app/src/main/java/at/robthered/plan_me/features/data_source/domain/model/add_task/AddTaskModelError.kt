package at.robthered.plan_me.features.data_source.domain.model.add_task

import android.os.Parcelable
import at.robthered.plan_me.features.data_source.domain.validation.TaskDescriptionValidationError
import at.robthered.plan_me.features.data_source.domain.validation.TaskTitleValidationError
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddTaskModelError(
    val title: TaskTitleValidationError? = null,
    val description: TaskDescriptionValidationError? = null,
) : Parcelable