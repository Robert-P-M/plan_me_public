package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation

import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.Time
import kotlinx.datetime.LocalDate

sealed interface TaskSchedulePickerDialogUiAction {
    data object OnNavigateBack : TaskSchedulePickerDialogUiAction
    data class OnPickLocalDate(val localDate: LocalDate?) : TaskSchedulePickerDialogUiAction
    data class OnPickTime(val time: Time?) : TaskSchedulePickerDialogUiAction
    data object OnToggleNotificationEnabled : TaskSchedulePickerDialogUiAction
    data object OnToggleIsFullDay : TaskSchedulePickerDialogUiAction
    data class OnPickDuration(val duration: Time?) : TaskSchedulePickerDialogUiAction
    data object OnSaveTaskSchedule : TaskSchedulePickerDialogUiAction
}