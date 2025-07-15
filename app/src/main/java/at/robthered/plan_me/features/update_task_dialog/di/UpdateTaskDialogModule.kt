package at.robthered.plan_me.features.update_task_dialog.di

import androidx.lifecycle.SavedStateHandle
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.helper.UseCaseOperator
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_description_history.CreateTaskDescriptionHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_priority_history.CreateTaskPriorityHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_title_history.CreateTaskTitleHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.load_update_task_model.LoadUpdateTaskModelUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.load_update_task_model.LoadUpdateTaskModelUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.update_task.UpdateTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.update_task.UpdateTaskUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.update_task_description.UpdateTaskDescriptionUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.update_task_priority.UpdateTaskPriorityUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.update_task_title.UpdateTaskTitleUseCase
import at.robthered.plan_me.features.data_source.domain.validation.TaskDescriptionValidator
import at.robthered.plan_me.features.data_source.domain.validation.TaskTitleValidator
import at.robthered.plan_me.features.data_source.domain.validation.update_task.UpdateTaskModelValidator
import at.robthered.plan_me.features.data_source.domain.validation.update_task.UpdateTaskModelValidatorImpl
import at.robthered.plan_me.features.priority_picker_dialog.presentation.priority_picker_event.PriorityPickerEventBus
import at.robthered.plan_me.features.update_task_dialog.presentation.UpdateTaskDialogViewModel
import at.robthered.plan_me.features.update_task_dialog.presentation.helper.CanSaveUpdateTaskChecker
import at.robthered.plan_me.features.update_task_dialog.presentation.helper.CanSaveUpdateTaskCheckerImpl
import at.robthered.plan_me.features.update_task_dialog.presentation.helper.UpdateTaskModelChangeChecker
import at.robthered.plan_me.features.update_task_dialog.presentation.helper.UpdateTaskModelChangeCheckerImpl
import at.robthered.plan_me.features.update_task_dialog.presentation.navigation.UpdateTaskDialogNavigationEventDispatcher
import at.robthered.plan_me.features.update_task_dialog.presentation.navigation.UpdateTaskDialogNavigationEventDispatcherImpl
import kotlinx.datetime.Clock
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val updateTaskDialogModule = module {

    factory<LoadUpdateTaskModelUseCase> {
        LoadUpdateTaskModelUseCaseImpl(
            localTaskRepository = get<LocalTaskRepository>()
        )
    }

    factory<CanSaveUpdateTaskChecker> {
        CanSaveUpdateTaskCheckerImpl()
    }


    factory<UpdateTaskUseCase> {
        UpdateTaskUseCaseImpl(
            transactionProvider = get<TransactionProvider>(),
            createTaskTitleHistoryUseCase = get<CreateTaskTitleHistoryUseCase>(),
            updateTaskTitleUseCase = get<UpdateTaskTitleUseCase>(),
            createTaskDescriptionHistoryUseCase = get<CreateTaskDescriptionHistoryUseCase>(),
            updateTaskDescriptionUseCase = get<UpdateTaskDescriptionUseCase>(),
            createTaskPriorityHistoryUseCase = get<CreateTaskPriorityHistoryUseCase>(),
            updateTaskPriorityUseCase = get<UpdateTaskPriorityUseCase>(),
            getTaskModelUseCase = get<GetTaskModelUseCase>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
            clock = get<Clock>()
        )
    }

    factory<UpdateTaskModelChangeChecker> {
        UpdateTaskModelChangeCheckerImpl()
    }

    factory<UpdateTaskModelValidator> {
        UpdateTaskModelValidatorImpl(
            taskTitleValidator = get<TaskTitleValidator>(),
            taskDescriptionValidator = get<TaskDescriptionValidator>()
        )
    }

    factory<UpdateTaskDialogNavigationEventDispatcher> {
        UpdateTaskDialogNavigationEventDispatcherImpl()
    }

    viewModel {
        UpdateTaskDialogViewModel(
            savedStateHandle = get<SavedStateHandle>(),
            loadUpdateTaskModelUseCase = get<LoadUpdateTaskModelUseCase>(),
            updateTaskModelChangeChecker = get<UpdateTaskModelChangeChecker>(),
            canSaveUpdateTaskChecker = get<CanSaveUpdateTaskChecker>(),
            updateTaskModelValidator = get<UpdateTaskModelValidator>(),
            updateTaskUseCase = get<UpdateTaskUseCase>(),
            useCaseOperator = get<UseCaseOperator>(),
            priorityPickerEventBus = get<PriorityPickerEventBus>(),
            updateTaskDialogNavigationEventDispatcher = get<UpdateTaskDialogNavigationEventDispatcher>(),
            appUiEventDispatcher = get<AppUiEventDispatcher>(),
        )
    }

}