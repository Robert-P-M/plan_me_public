package at.robthered.plan_me.features.task_details_dialog.di

import androidx.lifecycle.SavedStateHandle
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.helper.UseCaseOperator
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.use_case.add_task_schedule_event.AddTaskScheduleEventUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_priority.ChangeTaskPriorityUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task.DeleteTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_hashtag_reference.DeleteTaskHashtagReferenceUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_details_dialog_task.GetTaskDetailsDialogTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.load_task_roots.LoadTaskRootsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.load_task_roots.LoadTaskRootsUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_archive.ToggleArchiveTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_complete.ToggleCompleteTaskUseCase
import at.robthered.plan_me.features.priority_picker_dialog.presentation.priority_picker_event.PriorityPickerEventBus
import at.robthered.plan_me.features.task_details_dialog.presentation.TaskDetailsDialogViewModel
import at.robthered.plan_me.features.task_details_dialog.presentation.navigation.TaskDetailsDialogNavigationEventDispatcher
import at.robthered.plan_me.features.task_details_dialog.presentation.navigation.TaskDetailsDialogNavigationEventDispatcherImpl
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.task_schedule_event.TaskScheduleEventBus
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val taskDetailsDialogModule = module {

    factory<LoadTaskRootsUseCase> {
        LoadTaskRootsUseCaseImpl(
            localTaskRepository = get<LocalTaskRepository>(),
            localSectionRepository = get<LocalSectionRepository>()
        )
    }

    factory<TaskDetailsDialogNavigationEventDispatcher> {
        TaskDetailsDialogNavigationEventDispatcherImpl()
    }

    viewModel {
        TaskDetailsDialogViewModel(
            savedStateHandle = get<SavedStateHandle>(),
            loadTaskRootUseCase = get<LoadTaskRootsUseCase>(),
            toggleCompleteTaskUseCase = get<ToggleCompleteTaskUseCase>(),
            useCaseOperator = get<UseCaseOperator>(),
            priorityPickerEventBus = get<PriorityPickerEventBus>(),
            deleteTaskUseCase = get<DeleteTaskUseCase>(),
            toggleArchiveTaskUseCase = get<ToggleArchiveTaskUseCase>(),
            deleteTaskHashtagReferenceUseCase = get<DeleteTaskHashtagReferenceUseCase>(),
            changeTaskPriorityUseCase = get<ChangeTaskPriorityUseCase>(),
            taskDetailsDialogNavigationEventDispatcher = get<TaskDetailsDialogNavigationEventDispatcher>(),
            appUiEventDispatcher = get<AppUiEventDispatcher>(),
            taskScheduleEventBus = get<TaskScheduleEventBus>(),
            addTaskScheduleEventUseCase = get<AddTaskScheduleEventUseCase>(),
            getTaskDetailsDialogTaskUseCase = get<GetTaskDetailsDialogTaskUseCase>()
        )
    }
}