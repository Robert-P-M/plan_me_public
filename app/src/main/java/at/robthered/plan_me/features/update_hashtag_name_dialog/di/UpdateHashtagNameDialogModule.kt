package at.robthered.plan_me.features.update_hashtag_name_dialog.di

import androidx.lifecycle.SavedStateHandle
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.helper.UseCaseOperator
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagNameHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagRepository
import at.robthered.plan_me.features.data_source.domain.use_case.load_update_hashtag_model.LoadUpdateHashtagModelUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.load_update_hashtag_model.LoadUpdateHashtagModelUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.update_hashtag_name.UpdateHashtagNameUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.update_hashtag_name.UpdateHashtagNameUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.validation.HashtagNameValidator
import at.robthered.plan_me.features.data_source.domain.validation.update_hashtag_name.UpdateHashtagModelValidator
import at.robthered.plan_me.features.data_source.domain.validation.update_hashtag_name.UpdateHashtagModelValidatorImpl
import at.robthered.plan_me.features.update_hashtag_name_dialog.presentation.UpdateHashtagNameDialogViewModel
import at.robthered.plan_me.features.update_hashtag_name_dialog.presentation.helper.CanSaveUpdateHashtagModelChecker
import at.robthered.plan_me.features.update_hashtag_name_dialog.presentation.helper.CanSaveUpdateHashtagModelCheckerImpl
import at.robthered.plan_me.features.update_hashtag_name_dialog.presentation.helper.DidUpdateHashtagModelChangeChecker
import at.robthered.plan_me.features.update_hashtag_name_dialog.presentation.helper.DidUpdateHashtagModelChangeCheckerImpl
import at.robthered.plan_me.features.update_hashtag_name_dialog.presentation.navigation.UpdateHashtagNameDialogNavigationEventDispatcher
import at.robthered.plan_me.features.update_hashtag_name_dialog.presentation.navigation.UpdateHashtagNameDialogNavigationEventDispatcherImpl
import kotlinx.datetime.Clock
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val updateHashtagNameDialogModule = module {

    factory<UpdateHashtagNameDialogNavigationEventDispatcher> {
        UpdateHashtagNameDialogNavigationEventDispatcherImpl()
    }

    factory<DidUpdateHashtagModelChangeChecker> {
        DidUpdateHashtagModelChangeCheckerImpl()
    }

    factory<UpdateHashtagModelValidator> {
        UpdateHashtagModelValidatorImpl(
            hashtagNameValidator = get<HashtagNameValidator>()
        )
    }

    factory<CanSaveUpdateHashtagModelChecker> {
        CanSaveUpdateHashtagModelCheckerImpl()
    }

    single<LoadUpdateHashtagModelUseCase> {
        LoadUpdateHashtagModelUseCaseImpl(
            localHashtagRepository = get<LocalHashtagRepository>()
        )
    }

    single<UpdateHashtagNameUseCase> {
        UpdateHashtagNameUseCaseImpl(
            transactionProvider = get<TransactionProvider>(),
            localHashtagRepository = get<LocalHashtagRepository>(),
            localHashtagNameHistoryRepository = get<LocalHashtagNameHistoryRepository>(),
            clock = get<Clock>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>()
        )
    }

    viewModel<UpdateHashtagNameDialogViewModel>() {
        UpdateHashtagNameDialogViewModel(
            savedStateHandler = get<SavedStateHandle>(),
            loadUpdateHashtagModelUseCase = get<LoadUpdateHashtagModelUseCase>(),
            updateHashtagNameDialogNavigationEventDispatcher = get<UpdateHashtagNameDialogNavigationEventDispatcher>(),
            didUpdateHashtagModelChangeChecker = get<DidUpdateHashtagModelChangeChecker>(),
            canSaveUpdateHashtagModelChecker = get<CanSaveUpdateHashtagModelChecker>(),
            updateHashtagModelValidator = get<UpdateHashtagModelValidator>(),
            appUiEventDispatcher = get<AppUiEventDispatcher>(),
            useCaseOperator = get<UseCaseOperator>(),
            updateHashtagNameUseCase = get<UpdateHashtagNameUseCase>(),
        )
    }
}