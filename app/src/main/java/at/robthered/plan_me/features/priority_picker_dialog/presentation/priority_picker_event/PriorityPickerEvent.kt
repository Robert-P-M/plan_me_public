package at.robthered.plan_me.features.priority_picker_dialog.presentation.priority_picker_event

import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum

sealed class PriorityPickerEvent {
    data class NewPriority(val priorityEnum: PriorityEnum?) : PriorityPickerEvent()
    data class CurrentPriority(val priorityEnum: PriorityEnum?) : PriorityPickerEvent()
    data class CurrentTaskPriority(val priorityEnum: PriorityEnum?, val taskId: Long) :
        PriorityPickerEvent()

    data class NewTaskPriority(val priorityEnum: PriorityEnum?, val taskId: Long) :
        PriorityPickerEvent()

    data object ClearEvent : PriorityPickerEvent()
}