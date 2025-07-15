package at.robthered.plan_me.features.update_section_title_dialog.di

import androidx.lifecycle.SavedStateHandle
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.helper.UseCaseOperator
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionTitleHistoryRepository
import at.robthered.plan_me.features.data_source.domain.use_case.get_section_model.GetSectionModelUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.load_update_section_model.LoadUpdateSectionTitleModelUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.load_update_section_model.LoadUpdateSectionTitleModelUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.update_section_title.UpdateSectionTitleUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.update_section_title.UpdateSectionTitleUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.validation.SectionTitleValidator
import at.robthered.plan_me.features.data_source.domain.validation.update_section.UpdateSectionTitleModelValidator
import at.robthered.plan_me.features.data_source.domain.validation.update_section.UpdateSectionTitleModelValidatorImpl
import at.robthered.plan_me.features.update_section_title_dialog.presentation.UpdateSectionTitleDialogViewModel
import at.robthered.plan_me.features.update_section_title_dialog.presentation.helper.CanSaveUpdateSectionTitleChecker
import at.robthered.plan_me.features.update_section_title_dialog.presentation.helper.CanSaveUpdateSectionTitleCheckerImpl
import at.robthered.plan_me.features.update_section_title_dialog.presentation.helper.UpdateSectionTitleModelChangeChecker
import at.robthered.plan_me.features.update_section_title_dialog.presentation.helper.UpdateSectionTitleModelChangeCheckerImpl
import at.robthered.plan_me.features.update_section_title_dialog.presentation.navigation.UpdateSectionTitleDialogNavigationEventDispatcher
import at.robthered.plan_me.features.update_section_title_dialog.presentation.navigation.UpdateSectionTitleDialogNavigationEventDispatcherImpl
import kotlinx.datetime.Clock
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val updateSectionTitleDialogModule = module {

    factory<LoadUpdateSectionTitleModelUseCase> {
        LoadUpdateSectionTitleModelUseCaseImpl(
            localSectionRepository = get<LocalSectionRepository>()
        )
    }

    factory<UpdateSectionTitleUseCase> {
        UpdateSectionTitleUseCaseImpl(
            transactionProvider = get<TransactionProvider>(),
            localSectionRepository = get<LocalSectionRepository>(),
            localSectionTitleHistoryRepository = get<LocalSectionTitleHistoryRepository>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
            getSectionModelUseCase = get<GetSectionModelUseCase>(),
            clock = get<Clock>(),
        )
    }

    factory<UpdateSectionTitleModelChangeChecker> {
        UpdateSectionTitleModelChangeCheckerImpl()
    }
    factory<CanSaveUpdateSectionTitleChecker> {
        CanSaveUpdateSectionTitleCheckerImpl()
    }
    factory<UpdateSectionTitleModelValidator> {
        UpdateSectionTitleModelValidatorImpl(
            sectionTitleValidator = get<SectionTitleValidator>()
        )
    }

    factory<UpdateSectionTitleDialogNavigationEventDispatcher> {
        UpdateSectionTitleDialogNavigationEventDispatcherImpl()
    }
    viewModel {
        UpdateSectionTitleDialogViewModel(
            savedStateHandle = get<SavedStateHandle>(),
            loadUpdateSectionTitleModelUseCase = get<LoadUpdateSectionTitleModelUseCase>(),
            updateSectionTitleModelChangeChecker = get<UpdateSectionTitleModelChangeChecker>(),
            canSaveUpdateSectionTitleChecker = get<CanSaveUpdateSectionTitleChecker>(),
            updateSectionTitleModelValidator = get<UpdateSectionTitleModelValidator>(),
            updateSectionTitleUseCase = get<UpdateSectionTitleUseCase>(),
            useCaseOperator = get<UseCaseOperator>(),
            updateSectionTitleDialogNavigationEventDispatcher = get<UpdateSectionTitleDialogNavigationEventDispatcher>(),
            appUiEventDispatcher = get<AppUiEventDispatcher>(),
        )
    }
}