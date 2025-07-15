package at.robthered.plan_me.features.move_task_dialog.di

import androidx.lifecycle.SavedStateHandle
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.helper.UseCaseOperator
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.use_case.flattened_recurisve_task_loader.FlattenedRecursiveTaskLoader
import at.robthered.plan_me.features.data_source.domain.use_case.flattened_recurisve_task_loader.FlattenedRecursiveTaskLoaderImpl
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.load_move_task_items.LoadMoveTaskItemsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.load_move_task_items.LoadMoveTaskItemsUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.move_task.MoveTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.move_task.MoveTaskUseCaseImpl
import at.robthered.plan_me.features.move_task_dialog.presentation.MoveTaskDialogViewModel
import at.robthered.plan_me.features.move_task_dialog.presentation.navigation.MoveTaskDialogNavigationEventDispatcher
import at.robthered.plan_me.features.move_task_dialog.presentation.navigation.MoveTaskDialogNavigationEventDispatcherImpl
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val moveTaskDialogModule = module {

    factory<MoveTaskDialogNavigationEventDispatcher> {
        MoveTaskDialogNavigationEventDispatcherImpl()
    }

    factory<FlattenedRecursiveTaskLoader> {
        FlattenedRecursiveTaskLoaderImpl(
            localTaskRepository = get<LocalTaskRepository>()
        )
    }

    factory<LoadMoveTaskItemsUseCase> {
        LoadMoveTaskItemsUseCaseImpl(
            localTaskRepository = get<LocalTaskRepository>(),
            localSectionRepository = get<LocalSectionRepository>(),
            flattenedRecursiveTaskLoader = get<FlattenedRecursiveTaskLoader>()
        )
    }

    single<MoveTaskUseCase> {
        MoveTaskUseCaseImpl(
            localTaskRepository = get<LocalTaskRepository>(),
            transactionProvider = get<TransactionProvider>(),
            getTaskModelUseCase = get<GetTaskModelUseCase>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>()
        )
    }

    viewModel {
        MoveTaskDialogViewModel(
            savedStateHandle = get<SavedStateHandle>(),
            moveTaskDialogNavigationEventDispatcher = get<MoveTaskDialogNavigationEventDispatcher>(),
            appUiEventDispatcher = get<AppUiEventDispatcher>(),
            useCaseOperator = get<UseCaseOperator>(),
            moveTaskUseCase = get<MoveTaskUseCase>(),
            loadMoveTaskItemsUseCase = get<LoadMoveTaskItemsUseCase>()
        )
    }
}