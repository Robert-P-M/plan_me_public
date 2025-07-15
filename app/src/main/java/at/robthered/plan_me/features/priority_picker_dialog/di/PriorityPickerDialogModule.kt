package at.robthered.plan_me.features.priority_picker_dialog.di

import androidx.lifecycle.SavedStateHandle
import at.robthered.plan_me.features.priority_picker_dialog.presentation.PriorityPickerDialogViewModel
import at.robthered.plan_me.features.priority_picker_dialog.presentation.navigation.PriorityPickerDialogNavigationEventDispatcher
import at.robthered.plan_me.features.priority_picker_dialog.presentation.navigation.PriorityPickerDialogNavigationEventDispatcherImpl
import at.robthered.plan_me.features.priority_picker_dialog.presentation.priority_picker_event.PriorityPickerEventBus
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val priorityPickerDialogModule = module {
    factory<PriorityPickerDialogNavigationEventDispatcher> {
        PriorityPickerDialogNavigationEventDispatcherImpl()
    }
    viewModel {
        PriorityPickerDialogViewModel(
            savedStateHandle = get<SavedStateHandle>(),
            priorityPickerEventBus = get<PriorityPickerEventBus>(),
            priorityPickerDialogNavigationEventDispatcher = get<PriorityPickerDialogNavigationEventDispatcher>()
        )
    }
}