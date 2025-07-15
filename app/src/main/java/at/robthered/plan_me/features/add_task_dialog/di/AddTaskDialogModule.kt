package at.robthered.plan_me.features.add_task_dialog.di

import androidx.lifecycle.SavedStateHandle
import at.robthered.plan_me.features.add_task_dialog.presentation.AddTaskDialogViewModel
import at.robthered.plan_me.features.add_task_dialog.presentation.helper.AddTaskModelChangeChecker
import at.robthered.plan_me.features.add_task_dialog.presentation.helper.AddTaskModelChangeCheckerImpl
import at.robthered.plan_me.features.add_task_dialog.presentation.helper.CanSaveAddTaskChecker
import at.robthered.plan_me.features.add_task_dialog.presentation.helper.CanSaveAddTaskCheckerImpl
import at.robthered.plan_me.features.add_task_dialog.presentation.navigation.AddTaskDialogNavigationEventDispatcher
import at.robthered.plan_me.features.add_task_dialog.presentation.navigation.AddTaskDialogNavigationEventDispatcherImpl
import at.robthered.plan_me.features.common.domain.HashtagPickerEventBus
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.helper.UseCaseOperator
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.use_case.add_task.AddTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.add_task.AddTaskUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.add_task_schedule_event.AddTaskScheduleEventUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_description_history.CreateTaskDescriptionHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_hashtag_cross_ref.CreateTaskHashtagCrossRefUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_priority_history.CreateTaskPriorityHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_title_history.CreateTaskTitleHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.validation.TaskDescriptionValidator
import at.robthered.plan_me.features.data_source.domain.validation.TaskDescriptionValidatorImpl
import at.robthered.plan_me.features.data_source.domain.validation.TaskTitleValidator
import at.robthered.plan_me.features.data_source.domain.validation.TaskTitleValidatorImpl
import at.robthered.plan_me.features.data_source.domain.validation.add_task.AddTaskModelValidator
import at.robthered.plan_me.features.data_source.domain.validation.add_task.AddTaskModelValidatorImpl
import at.robthered.plan_me.features.priority_picker_dialog.presentation.priority_picker_event.PriorityPickerEventBus
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.task_schedule_event.TaskScheduleEventBus
import kotlinx.datetime.Clock
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val addTaskDialogModule = module {

    single<AddTaskUseCase> {
        AddTaskUseCaseImpl(
            transactionProvider = get<TransactionProvider>(),
            localTaskRepository = get<LocalTaskRepository>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
            clock = get<Clock>(),
            createTaskTitleHistoryUseCase = get<CreateTaskTitleHistoryUseCase>(),
            createTaskDescriptionHistoryUseCase = get<CreateTaskDescriptionHistoryUseCase>(),
            createTaskPriorityHistoryUseCase = get<CreateTaskPriorityHistoryUseCase>(),
            createTaskHashtagCrossRefUseCase = get<CreateTaskHashtagCrossRefUseCase>(),
            addTaskScheduleEventUseCase = get<AddTaskScheduleEventUseCase>(),
        )
    }

    factory<TaskTitleValidator> {
        TaskTitleValidatorImpl()
    }
    factory<TaskDescriptionValidator> {
        TaskDescriptionValidatorImpl()
    }

    factory<AddTaskModelValidator> {
        AddTaskModelValidatorImpl(
            taskTitleValidator = get<TaskTitleValidator>(),
            taskDescriptionValidator = get<TaskDescriptionValidator>()
        )
    }

    factory<CanSaveAddTaskChecker> {
        CanSaveAddTaskCheckerImpl()
    }
    factory<AddTaskModelChangeChecker> {
        AddTaskModelChangeCheckerImpl()
    }

    factory<AddTaskDialogNavigationEventDispatcher> {
        AddTaskDialogNavigationEventDispatcherImpl()
    }

    viewModel {
        AddTaskDialogViewModel(
            savedStateHandle = get<SavedStateHandle>(),
            addTaskModelValidator = get<AddTaskModelValidator>(),
            addTaskModelChangeChecker = get<AddTaskModelChangeChecker>(),
            canSaveAddTaskChecker = get<CanSaveAddTaskChecker>(),
            addTaskUseCase = get<AddTaskUseCase>(),
            priorityPickerEventBus = get<PriorityPickerEventBus>(),
            hashtagPickerEventBus = get<HashtagPickerEventBus>(),
            taskScheduleEventBus = get<TaskScheduleEventBus>(),
            addTaskDialogNavigationEventDispatcher = get<AddTaskDialogNavigationEventDispatcher>(),
            appUiEventDispatcher = get<AppUiEventDispatcher>(),
            useCaseOperator = get<UseCaseOperator>()
        )
    }

}