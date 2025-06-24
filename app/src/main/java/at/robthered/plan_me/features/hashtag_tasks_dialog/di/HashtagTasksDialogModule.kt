package at.robthered.plan_me.features.hashtag_tasks_dialog.di

import androidx.lifecycle.SavedStateHandle
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.helper.UseCaseOperator
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_hashtag_reference.DeleteTaskHashtagReferenceUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.load_hashtags_with_tasks.LoadHashtagWithTasksUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_complete.ToggleCompleteTaskUseCase
import at.robthered.plan_me.features.hashtag_tasks_dialog.presentation.HashtagTasksDialogViewModel
import at.robthered.plan_me.features.hashtag_tasks_dialog.presentation.navigation.HashtagTasksDialogNavigationEventDispatcher
import at.robthered.plan_me.features.hashtag_tasks_dialog.presentation.navigation.HashtagTasksDialogNavigationEventDispatcherImpl
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val hashtagTasksDialogModule = module {

    factory<HashtagTasksDialogNavigationEventDispatcher> {
        HashtagTasksDialogNavigationEventDispatcherImpl()
    }

    viewModel {
        HashtagTasksDialogViewModel(
            savedStateHandle = get<SavedStateHandle>(),
            hashtagTasksDialogNavigationEventDispatcher = get<HashtagTasksDialogNavigationEventDispatcher>(),
            deleteTaskHashtagReferenceUseCase = get<DeleteTaskHashtagReferenceUseCase>(),
            loadHashtagWithTasksUseCase = get<LoadHashtagWithTasksUseCase>(),
            toggleCompleteTaskUseCase = get<ToggleCompleteTaskUseCase>(),
            useCaseOperator = get<UseCaseOperator>(),
            appUiEventDispatcher = get<AppUiEventDispatcher>()
        )
    }
}