package at.robthered.plan_me.features.common.presentation.navigation

import android.os.Parcelable
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

sealed class Route {

    @Serializable
    data object InboxScreen : Route()

    @Serializable
    data object AddSectionDialog : Route()

    @Serializable
    data class AddTaskDialog(
        val sectionId: Long? = null,
        val parentTaskId: Long? = null,
    ) : Route()

    @Serializable
    data object PriorityPickerDialog : Route()

    @Serializable
    data class UpdateSectionTitleDialog(
        val sectionId: Long,
    ) : Route()

    @Serializable
    data class UpdateTaskDialog(
        val taskId: Long,
    ) : Route()

    @Serializable
    data class TaskDetailsDialog(
        val taskId: Long,
    ) : Route()

    @Serializable
    data class SectionDetailsDialog(
        val sectionId: Long,
    ) : Route()

    @Serializable
    data class TaskStatisticsDialog(
        val taskId: Long,
    ) : Route()

    @Serializable
    data object HashtagPickerDialog : Route()

    @Serializable
    data class UpdateHashtagNameDialog(
        val hashtagId: Long,
    ) : Route()

    @Serializable
    data class TaskHashtagsDialog(
        val taskId: Long,
    ) : Route()

    @Serializable
    data class HashtagTasksDialog(
        val hashtagId: Long,
    ) : Route()

    @Serializable
    data class MoveTaskDialog(
        val taskId: Long,
        val parentTaskId: Long? = null,
        val sectionId: Long? = null,
    ) : Route()

    @Serializable
    data class TaskSchedulePickerDialog(
        val taskId: Long = 0,
        val startDateInEpochDays: Int? = null,
        val timeOfDayInMinutes: Int? = null,
        val isNotificationEnabled: Boolean = false,
        val durationInMinutes: Int? = null,
        val isFullDay: Boolean = false,
    ) : Route()


}

@Parcelize
data class TaskSchedulePickerDialogArgs(
    val taskId: Long = 0,
    val startDateInEpochDays: Int? = null,
    val timeOfDayInMinutes: Int? = null,
    val isNotificationEnabled: Boolean = false,
    val durationInMinutes: Int? = null,
    val isFullDay: Boolean = false,
) : Parcelable

@Parcelize
data class MoveTaskDialogArgs(
    val taskId: Long,
    val parentTaskId: Long?,
    val sectionId: Long?,
) : Parcelable

data class HashtagTasksDialogArgs(
    val hashtagId: Long,
)

data class TaskHashtagsDialogArgs(
    val taskId: Long,
)

data class AddTaskDialogArgs(
    val parentTaskId: Long? = null,
    val sectionId: Long? = null,
)

data class UpdateSectionTitleDialogArgs(
    val sectionId: Long,
)

data class UpdateTaskDialogArgs(
    val taskId: Long,
)


data class PriorityPickerUpdateArgs(
    val taskId: Long,
    val priorityEnum: PriorityEnum? = null,
)

data class TaskDetailsDialogArgs(
    val taskId: Long,
)

data class TaskStatisticsDialogArgs(
    val taskId: Long,
)

data class UpdateHashtagNameDialogArgs(
    val hashtagId: Long,
)