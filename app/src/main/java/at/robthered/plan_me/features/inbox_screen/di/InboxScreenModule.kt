package at.robthered.plan_me.features.inbox_screen.di

import androidx.lifecycle.SavedStateHandle
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.helper.UseCaseOperator
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskHashtagsCrossRefRepository
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_priority.ChangeTaskPriorityUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_section.DeleteSectionUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task.DeleteTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_hashtag_reference.DeleteTaskHashtagReferenceUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_hashtag_reference.DeleteTaskHashtagReferenceUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.duplicate_task.DuplicateTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_inbox_screen_items.GetInboxScreenItemsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_archive.ToggleArchiveTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_complete.ToggleCompleteTaskUseCase
import at.robthered.plan_me.features.inbox_screen.presentation.InboxScreenViewModel
import at.robthered.plan_me.features.inbox_screen.presentation.navigation.InboxScreenNavigationEventDispatcher
import at.robthered.plan_me.features.inbox_screen.presentation.navigation.InboxScreenNavigationEventDispatcherImpl
import at.robthered.plan_me.features.priority_picker_dialog.presentation.priority_picker_event.PriorityPickerEventBus
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val inboxScreenModule = module {


    factory<InboxScreenNavigationEventDispatcher> {
        InboxScreenNavigationEventDispatcherImpl()
    }

    factory<DeleteTaskHashtagReferenceUseCase> {
        DeleteTaskHashtagReferenceUseCaseImpl(
            localTaskHashtagsCrossRefRepository = get<LocalTaskHashtagsCrossRefRepository>(),
            transactionProvider = get<TransactionProvider>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>()
        )
    }

    viewModel {
        InboxScreenViewModel(
            savedStateHandle = get<SavedStateHandle>(),
            toggleCompleteTaskUseCase = get<ToggleCompleteTaskUseCase>(),
            deleteTaskUseCase = get<DeleteTaskUseCase>(),
            deleteSectionUseCase = get<DeleteSectionUseCase>(),
            toggleArchiveTaskUseCase = get<ToggleArchiveTaskUseCase>(),
            priorityPickerEventBus = get<PriorityPickerEventBus>(),
            changeTaskPriorityUseCase = get<ChangeTaskPriorityUseCase>(),
            duplicateTaskUseCase = get<DuplicateTaskUseCase>(),
            inboxScreenNavigationEventDispatcher = get<InboxScreenNavigationEventDispatcher>(),
            appUiEventDispatcher = get<AppUiEventDispatcher>(),
            getInboxScreenItemsUseCase = get<GetInboxScreenItemsUseCase>(),
            useCaseOperator = get<UseCaseOperator>()
        )
    }
}