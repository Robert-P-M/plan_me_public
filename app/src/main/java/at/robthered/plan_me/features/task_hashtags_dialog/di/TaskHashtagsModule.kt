package at.robthered.plan_me.features.task_hashtags_dialog.di

import androidx.lifecycle.SavedStateHandle
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.helper.UseCaseOperator
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskHashtagsCrossRefRepository
import at.robthered.plan_me.features.data_source.domain.use_case.add_existing_hashtag.AddExistingHashtagUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.add_existing_hashtag.AddExistingHashtagUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.add_hashtag_helper.AddHashtagHelper
import at.robthered.plan_me.features.data_source.domain.use_case.add_new_hashtag.AddNewHashtagUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.add_new_hashtag.AddNewHashtagUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_hashtag_cross_ref.CreateTaskHashtagCrossRefUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_hashtag_cross_ref.CreateTaskHashtagCrossRefUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_hashtag_reference.DeleteTaskHashtagReferenceUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_hashtag_by_name.GetHashtagsByNameUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.load_hashtags_for_task.LoadHashtagsForTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.load_hashtags_for_task.LoadHashtagsForTaskUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.validation.new_hashtag.NewHashtagModelValidator
import at.robthered.plan_me.features.hashtag_picker_dialog.presentation.helper.CanSaveNewHashtagModelChecker
import at.robthered.plan_me.features.task_hashtags_dialog.presentation.TaskHashtagsDialogViewModel
import at.robthered.plan_me.features.task_hashtags_dialog.presentation.navigation.TaskHashtagsDialogNavigationDispatcher
import at.robthered.plan_me.features.task_hashtags_dialog.presentation.navigation.TaskHashtagsDialogNavigationDispatcherImpl
import kotlinx.datetime.Clock
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val taskHashtagsModule = module {

    factory<LoadHashtagsForTaskUseCase> {
        LoadHashtagsForTaskUseCaseImpl(
            localHashtagRepository = get<LocalHashtagRepository>()
        )
    }
    factory<TaskHashtagsDialogNavigationDispatcher> {
        TaskHashtagsDialogNavigationDispatcherImpl()
    }
    factory<AddExistingHashtagUseCase> {
        AddExistingHashtagUseCaseImpl(
            transactionProvider = get<TransactionProvider>(),
            localTaskHashtagsCrossRefRepository = get<LocalTaskHashtagsCrossRefRepository>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
            clock = get<Clock>(),
        )
    }
    single<CreateTaskHashtagCrossRefUseCase> {
        CreateTaskHashtagCrossRefUseCaseImpl(
            addHashtagHelper = get<AddHashtagHelper>(),
            localTaskHashtagsCrossRefRepository = get<LocalTaskHashtagsCrossRefRepository>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
            clock = get<Clock>()
        )
    }
    factory<AddNewHashtagUseCase> {
        AddNewHashtagUseCaseImpl(
            transactionProvider = get<TransactionProvider>(),
            localTaskHashtagsCrossRefRepository = get<LocalTaskHashtagsCrossRefRepository>(),
            addHashtagHelper = get<AddHashtagHelper>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
            clock = get<Clock>()
        )
    }
    viewModel {
        TaskHashtagsDialogViewModel(
            savedStateHandle = get<SavedStateHandle>(),
            taskHashtagsDialogNavigationDispatcher = get<TaskHashtagsDialogNavigationDispatcher>(),
            deleteTaskHashtagReferenceUseCase = get<DeleteTaskHashtagReferenceUseCase>(),
            loadHashtagsForTaskUseCase = get<LoadHashtagsForTaskUseCase>(),
            useCaseOperator = get<UseCaseOperator>(),
            appUiEventDispatcher = get<AppUiEventDispatcher>(),
            getHashtagsByNameUseCase = get<GetHashtagsByNameUseCase>(),
            newHashtagModelValidator = get<NewHashtagModelValidator>(),
            canSaveNewHashtagModelChecker = get<CanSaveNewHashtagModelChecker>(),
            addExistingHashtagUseCase = get<AddExistingHashtagUseCase>(),
            addNewHashtagUseCase = get<AddNewHashtagUseCase>(),
        )
    }
}